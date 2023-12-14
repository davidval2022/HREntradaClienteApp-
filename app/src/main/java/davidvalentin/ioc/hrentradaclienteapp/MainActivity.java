package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
///
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import davidvalentin.ioc.hrentradaclienteapp.login.ConexionAsyn;
import davidvalentin.ioc.hrentradaclienteapp.login.SocketManager;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

/**
 * @author David Valentin Mateo
 *  Activity principal, es el menú de login. Desde aquí una vez logeados iremos al menú que corresponda que pueden ser
 *  2, o menu de usuario normal o menu de usuario administrador
 */


public class MainActivity extends AppCompatActivity {

    EditText editIp;
    EditText editUsuario;
    EditText editPass;
    EditText editPuerto;
    TextView textLogeado;
    String ip = "192.168.1.12";
    String usuario = "0";
    String pass = "0";
    String puerto = "8888";
    String codigoGlobal = "0";
    Socket socket;
    ObjectOutputStream outObjeto;
    ObjectInputStream inObjeto;
    static String log_msg;
    static String mensaje;
    boolean salir = false;
    //static String codigo = "0";
    String mensajeLogeado = "";
    Button btnMenu;
    Button btnEnviar;
    Boolean redActiva = false;

    ConexionAsyn conexionAsyn = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editIp = findViewById(R.id.edit_ip);
        editUsuario = findViewById((R.id.edit_usuario));
        editPass = findViewById(R.id.edit_pass);
        editPuerto = findViewById(R.id.edit_puerto);
        textLogeado = findViewById(R.id.TextViewOkLogin);
        btnMenu = findViewById(R.id.btn_menu);
        btnEnviar = findViewById(R.id.btn_enviar);


        //iniciamos el sharedPreferences y añadimos la ip guardada
        cargarIpDeShared();

        //esta parte es para que en el hilo principal me deje hacer la conexion..no es
        //la mejor manera pero funiona.. además en teoria debería funcionar sin esto pero no se
        //porqué no me funciona
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
              .permitNetwork().build());




    }


    /**
     * Método desde el que recogemos los datos de los editText y se los enviamos al server.
     * Para esto utilizamos una instancia de una clase  de tipo Asyntask , para así poder modificar
     * la pantalla una vez logeados.
     * Está asociado al botón 'enviar'
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void enviarDatosLogin(View view){
        ip = editIp.getText().toString();
        usuario = editUsuario.getText().toString();
        pass = editPass.getText().toString();
        //hacemos el login
        try{
            //esta parte de abajo es para comprobar si la red funciona
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connMgr != null) {
                networkInfo = connMgr.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isConnected()){
                //mostrarToast("Valor de redActiva: "+redActiva);
                redActiva = true;
                Utilidades.socketManager = new SocketManager(ip,Integer.parseInt(puerto),this );
                Utilidades.socketManager.openSocket();
                conexionAsyn = new ConexionAsyn(Utilidades.socketManager,usuario,pass,this,textLogeado,btnMenu,btnEnviar);
                conexionAsyn.execute();
                guardarIpEnShared();

            }else{
                //mostrarToast("Valor de redActiva: "+redActiva);
            }
            //esto es para quitar el teclado cuando no se está usando
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputManager != null ) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            //aunque respecto a esto de arriba, lo de esconder el teclado la forma de hacerlo es desde
            //el archivo manifest añadiendo esto a la activity (ver el archivo manifest)
            //android:windowSoftInputMode="stateHidden"

        }catch(Exception e){
            Log.d("Error","Errores en login: "+e);
        }

    }

    /**
     * Método para borrar los datos de los campos de texto (EditText), esta asociado al botón
     * 'cancelar'
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void cancelar(View view){
        ip = "127.0.0.1";
        usuario = "0";
        pass = "0";
        editIp.setText("");
        editUsuario.setText("");
        editPass.setText("");
        Toast.makeText(this, "Introduce los datos para conectar", Toast.LENGTH_SHORT).show();
    }

    /**
     * Metodo que lanza un mensaje de Toast
     * @param mensaje es el mensaje que mostrará el Toast
     */
    public  void mostrarToast(String mensaje){
        Toast.makeText(this, "Mensaje: "+mensaje, Toast.LENGTH_SHORT).show();

    }

    /*
    public void login(View view){
        try{

            //si hay conexion y estamos conectados y la consulta no está vacia
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputManager != null ) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            if (redActiva){
                Utilidades.socketManager = new SocketManager(ip,Integer.parseInt(puerto) );
                Utilidades.socketManager.openSocket();
                ConexionAsyn conexionAsyn = new ConexionAsyn(Utilidades.socketManager,usuario,pass,getApplicationContext(),textLogeado,btnMenu,btnEnviar);
                conexionAsyn.execute();

            }



            mostrarToast("Valor de redActiva: "+redActiva);


        }catch(Exception e){
            Log.d("Error","Errores en login: "+e);
        }

    }

    public void logout(){
        try{
            LogoutAsyn logout = new LogoutAsyn(Utilidades.socketManager,getApplicationContext());
            Log.d("Correcto","Voy a LogoutAsyn ");
        }catch(Exception e){
            Log.d("Error","Errores en logout: "+e);
        }

    }
    */


    /**
     * Cuando nos logeamos en un server correctamente se guarda la ip del server. Este metodo sirve
     * para cargar esa ip y no tener que introducirla cada vez.. es util si el server
     * habitualmente va a tener la misma ip
     */
    public void cargarIpDeShared(){
        SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
        this.editIp.setText(preferencias.getString("ip",Utilidades.ip));

    }

    /**
     *  Metodo que guarda el contenido del campo ip en SharedPreferences. Al volver a abrir
     *  el programa, esa ip ya se introducira automaticamente con el metodo cargarIpDeShared
     */
    public void guardarIpEnShared() {
        SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("ip", editIp.getText().toString());
        editor.commit();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Utilidades.socketManager != null){
            Utilidades.socketManager.closeSocket();
        }

    }

    /**
     * Este método está asociado al botón 'ir al menu', y dependendiendo del tipo de usuario
     * que sea, lo enviará a un menú o otro.
     * @param v representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void menu(View v){

       // mostrarToast(Utilidades.codigo+ " El usuario es de tipo: "+Utilidades.tipoUser);
        if(Utilidades.tipoUser == 0){
            Intent intent = new Intent(this, MenuAdminActivity.class);
            startActivity(intent);
        }else if(Utilidades.tipoUser == 1){
            Intent intent = new Intent(this, MenuUserActivity.class);
            startActivity(intent);
        }else{
            mostrarToast( " Error en el tipo de usuario: "+Utilidades.tipoUser);
        }

    }





}