package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
///
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.Conexion;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SocketManager;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;
import model.LoginConsulta;


public class MainActivity extends AppCompatActivity {

    EditText editIp;
    EditText editUsuario;
    EditText editPass;
    EditText editPuerto;
    String ip = "192.168.1.12";
    String usuario = "0";
    String pass = "0";
    String puerto = "8888";
    LoginConsulta lconsulta;
    String codigoGlobal = "0";
    Socket socket;
    ObjectOutputStream outObjeto;
    ObjectInputStream inObjeto;
    static String log_msg;
    static String mensaje;
    boolean salir = false;
    //static String codigo = "0";

    static SocketManager socketManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lconsulta = new LoginConsulta();
        editIp = findViewById(R.id.edit_ip);
        editUsuario = findViewById((R.id.edit_usuario));
        editPass = findViewById(R.id.edit_pass);
        editPuerto = findViewById(R.id.edit_puerto);



        //iniciamos el sharedPreferences y añadimos la ip guardada
        cargarIpDeShared(editIp);

        //esta parte es para que en el hilo principal me deje hacer la conexion..no es
        //la mejor manera pero funiona..
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
               .permitNetwork().build());


    }



    public void enviarDatosLogin(View view){
        ip = editIp.getText().toString();
        usuario = editUsuario.getText().toString();
        pass = editPass.getText().toString();
        //hacemos el login
        login3();







    }

    public void cancelar(View view){
        ip = "127.0.0.1";
        usuario = "0";
        pass = "0";
        editIp.setText("");
        editUsuario.setText("");
        editPass.setText("");
        Toast.makeText(this, "Introduce los datos para conectar", Toast.LENGTH_SHORT).show();
    }


    public  void mostrarToast(String mensaje){
        Toast.makeText(this, "Mensaje: "+mensaje, Toast.LENGTH_SHORT).show();

    }
    /*
    public void login2(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // login(); //Realizar aquí tu proceso!
                    try{
                        socket = new Socket(ip, 8888);
                        BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));//flujo lectura del server
                        BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//flujo envio al server
                        //en principio lo mostraré en el log
                        String mensajeServer = lector.readLine();   //leemos el mensaje de bienvenidoa del server
                        Log.d(log_msg, "Mensaje del server 1: "+mensajeServer);
                        mostrarToast("Mensaje del server 1: "+mensajeServer);
                        //ahora escribimos en servidor , enviandole el login
                        escriptor.write(usuario+":"+pass);
                        escriptor.newLine();
                        escriptor.flush();
                        //leemos la respuesta, nos enviará un codigo
                        mensajeServer = lector.readLine();   //leemos ya la respuesta del server,    nos envia un código
                        Log.d(log_msg, "Mensaje del server 2: "+mensajeServer);

                        if(mensajeServer.equalsIgnoreCase("-1")){
                            System.out.println("Codigo = -1 .El login es erroneo");//vemos el código
                            Log.d(log_msg, "Mensaje del server 2: "+"Codigo = -1 .El login es erroneo");
                            salir = true;
                            lector.close();
                            escriptor.close();
                            socket.close();

                        }else if(mensajeServer.equalsIgnoreCase("-2")){
                            System.out.println("Codigo = -2 .El usuario ya esta conectado");//vemos el código
                            Log.d(log_msg, "Mensaje del server 2: "+"Codigo = -2 .El usuario ya esta conectado");
                            salir = true;
                            lector.close();
                            escriptor.close();
                            socket.close();
                        }else{
                            guardarIpEnShared(editIp);
                            Utilidades.codigo = mensajeServer;
                            Log.d(log_msg, "Codigo usuario válido = "+Utilidades.codigo);
                            mostrarToast("Codigo = "+Utilidades.codigo);
                        }


                        socket.close();

                    }catch(IOException e) {
                        Log.d(log_msg, "Error entrada salida: " + e);
                    }



                } catch (Exception e) {
                    Log.e("Error", "Exception: " + e.getMessage());
                }
            }
        });


    }
    public void login(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // login(); //Realizar aquí tu proceso!
                    try{
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(ip,Integer.parseInt(puerto)),4000);
                        BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));//flujo lectura del server
                        BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//flujo envio al server
                        //en principio lo mostraré en el log
                        String mensajeServer = lector.readLine();   //leemos el mensaje de bienvenidoa del server
                        Log.d(log_msg, "Mensaje del server 1: "+mensajeServer);
                        mostrarToast("Mensaje del server 1: "+mensajeServer);
                        //ahora escribimos en servidor , enviandole el login
                        escriptor.write(usuario+":"+pass);
                        escriptor.newLine();
                        escriptor.flush();
                        //leemos la respuesta, nos enviará un codigo
                        mensajeServer = lector.readLine();   //leemos ya la respuesta del server,    nos envia un código
                        Log.d(log_msg, "Mensaje del server 2: "+mensajeServer);

                        if(mensajeServer.equalsIgnoreCase("-1")){
                            System.out.println("Codigo = -1 .El login es erroneo");//vemos el código
                            Log.d(log_msg, "Mensaje del server 2: "+"Codigo = -1 .El login es erroneo");
                            salir = true;
                            lector.close();
                            escriptor.close();
                            socket.close();

                        }else if(mensajeServer.equalsIgnoreCase("-2")){
                            System.out.println("Codigo = -2 .El usuario ya esta conectado");//vemos el código
                            Log.d(log_msg, "Mensaje del server 2: "+"Codigo = -2 .El usuario ya esta conectado");
                            salir = true;
                            lector.close();
                            escriptor.close();
                            socket.close();
                        }else{
                            guardarIpEnShared(editIp);
                            Utilidades.codigo = mensajeServer;
                            Log.d(log_msg, "Codigo usuario válido = "+Utilidades.codigo);
                            mostrarToast("Codigo = "+Utilidades.codigo);
                        }


                        socket.close();

                    }catch(IOException e) {
                        Log.d(log_msg, "Error entrada salida: " + e);
                    }



                } catch (Exception e) {
                    Log.e("Error", "Exception: " + e.getMessage());
                }
            }
        });


    }
*/

    public void login3(){
        try{
            socketManager = new SocketManager(ip,Integer.parseInt(puerto) );
            socketManager.openSocket();
            Conexion conexion = new Conexion(socketManager,usuario,pass,getApplicationContext());
            conexion.execute();

            //mostrarToast("Codigo en MainActivity: "+Utilidades.codigo);


        }catch(Exception e){
            Log.d("Error","Errores en login3: "+e);
        }

    }




    public void cargarIpDeShared(View v){
        SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
        editIp.setText(preferencias.getString("ip",Utilidades.ip));

    }
    public void guardarIpEnShared(View v) {
        SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("ip", editIp.getText().toString());
        editor.commit();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketManager.closeSocket();
    }


    public void pruebas(View v){
        mostrarToast(Utilidades.codigo);
    }





}