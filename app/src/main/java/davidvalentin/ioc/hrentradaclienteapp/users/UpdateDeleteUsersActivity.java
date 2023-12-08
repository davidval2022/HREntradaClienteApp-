package davidvalentin.ioc.hrentradaclienteapp.users;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import davidvalentin.ioc.hrentradaclienteapp.R;
import davidvalentin.ioc.hrentradaclienteapp.users.UsersActivity;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;
import modelo.Users;
/**
 * Activity asociada a la modificacion de  users existentes. En la parte gráfica tenemos un formulario
 * donde introduciremos las modificaciones y luego a traves de esta activity seran tratados y enviados
 * al server
 */
public class UpdateDeleteUsersActivity extends AppCompatActivity {

    private Bundle userRecibido;//recibidos el bundle desde SelectUsersAsyn
    private Users user;//la utilizamos com variable users que mostramos/modificamos

    private Socket socket;
    private String nombreTabla = "1";//users es 0
    private String crud = "2";//update es el codigo crud 2
    private String orden = "0";//orden no lo utilizamos por lo tanto es siempres será 0
    private String loginOriginal = "";//login original, lo utilizaremos en el envio de datos, campo base en consulta
    private String dniOriginal = "";//dni original, lo utilizaremos en el envio de datos, campo base en consulta
    private String passOriginal = "";//Si la pass es diferente de que obtendremos como original la cambiamos

    private EditText editTextLogin;
    private EditText editTextPassUser;
    private EditText editTextTipoUser;
    private EditText editTextDniUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_users);

        socket = Utilidades.socketManager.getSocket();

        editTextLogin = findViewById(R.id.editTextLoginUpDel);
        editTextPassUser = findViewById(R.id.editTextPassUserUpDel);
        editTextTipoUser = findViewById(R.id.editTextTipoUserUpDel);
        editTextDniUser = findViewById(R.id.editTextDniUserUpDel);

        //parte para rellenar los campos con los datos recibidos de SelectEmpleadosAsyn
        userRecibido = getIntent().getExtras();
        user = null;
        if(userRecibido != null){
            //guardamos en la variable empresa la empresa recibida
            user=(Users) userRecibido.getSerializable("user");
            //nos quedamos con el nombre original, para luego saber a que registro hacemos el update
            loginOriginal = user.getLogin();
            dniOriginal = user.getDni();
            passOriginal = user.getPass();

            editTextDniUser.setText(user.getDni());
            editTextLogin.setText(user.getLogin());
            //editTextPassUser.setText(user.getPass());
            //editTextPassUser.setText("");
            editTextTipoUser.setText(String.valueOf(user.getNumtipe()));

        }
    }

    /**
     * Metodo asociado al boton 'guardar user'. Mediante este método enviamos los datos al
     * server para actualizar un  user. En este caso y a diferencia de cuando haciamos los
     * select, al no cambiar la pantalla en este justo momento no necesitaremos clases de tipo
     * Asyntask, o hacerlo en un hilo diferente del principal que maneja la UI.
     * Además solo podemos modificar, o la contraseña o el tipo de empledo, ni login ni dni
     * para eso creamos un nuevo y borramos este
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void actualizarUsuario(View view) {
        try {

            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                String codigo = "0";
                String numtipe = "0";
                String pass = "0";


                //solo nos quedaremos con los datos de numtipe y pass ya que el resto de los
                //campos no se modifica
                numtipe = editTextTipoUser.getText().toString();
                pass = editTextPassUser.getText().toString();
                //si la pass esta vacia, ponemos la pass anterior, es decir que no la cambiamos


                if(!dniOriginal.equals("") && !numtipe.equals("") && !pass.equals("") && !loginOriginal.equals("")){
                    String palabra = Utilidades.codigo+","+crud+","+nombreTabla+",passNuevo,"+pass+",numtipeNuevo,"+numtipe+",login,"+loginOriginal+","+orden;
                    //ahora escribimos en servidor , enviandole el login
                    escriptor.write(palabra);
                    escriptor.newLine();
                    escriptor.flush();
                    Log.d("Enviado", "Le enviamos esto al server: "+palabra);
                    if(palabra.equalsIgnoreCase("exit")){
                        lector.close();
                        escriptor.close();
                        socket.close();
                    }else{
                        perEnt = new ObjectInputStream(socket.getInputStream());
                        //leemos los datos del objeto y comprobamos que sea un arrayList, sino un String
                        Object receivedData = perEnt.readObject();

                        if (receivedData instanceof List) {
                            Utilidades.mensajeDelServer = "Se ha modificado correctamente el usuario";
                        } else if (receivedData instanceof String) {
                            Utilidades.mensajeDelServer = (String) receivedData;
                            Log.d("Recibido",Utilidades.mensajeDelServer);
                        } else {
                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                        }
                        mostrarToast(Utilidades.mensajeDelServer );

                    }
                }else{
                    mostrarToast("Tienen que estar rellenos el campo contraseña y tipo de usuario" );
                }


            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Metodo asociado al botón 'volver'. Con este método somo redirigidos a
     * UsersActivity
     */
    public void volver(View view) {
        Intent intent = new Intent(this, UsersActivity.class);
        startActivity(intent);
    }

    /**
     * Metodo que lanza un mensaje de Toast
     * @param mensaje es el mensaje que mostrará el Toast
     */
    public  void mostrarToast(String mensaje){
        Toast.makeText(this, ""+mensaje, Toast.LENGTH_LONG).show();


    }

    /**
     *
     * @param password
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String hashPassword(String password)  {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodedHash = md.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public void eliminarUser(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar acción")
                .setMessage("¿Estás seguro de que quieres eliminar este usuario?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            if (socket != null && socket.isConnected()) {
                                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                ObjectInputStream perEnt;

                                String codigo = "0";
                                String crud = "3";
                                String dni = editTextDniUser.getText().toString();


                                //si la pass esta vacia, ponemos la pass anterior, es decir que no la cambiamos


                                if(!dni.equals("")){
                                    String palabra = Utilidades.codigo+","+crud+","+nombreTabla+",dni,"+dni+","+orden;
                                    //ahora escribimos en servidor , enviandole el login
                                    escriptor.write(palabra);
                                    escriptor.newLine();
                                    escriptor.flush();
                                    Log.d("Enviado", "Le enviamos esto al server: "+palabra);
                                    if(palabra.equalsIgnoreCase("exit")){
                                        lector.close();
                                        escriptor.close();
                                        socket.close();
                                    }else{
                                        perEnt = new ObjectInputStream(socket.getInputStream());
                                        //leemos los datos del objeto y comprobamos que sea un arrayList, sino un String
                                        Object receivedData = perEnt.readObject();

                                        if (receivedData instanceof List) {
                                            Utilidades.mensajeDelServer = "Se ha eliminado correctamente el usuario";
                                        } else if (receivedData instanceof String) {
                                            Utilidades.mensajeDelServer = (String) receivedData;
                                            volver(view);
                                            Log.d("Recibido",Utilidades.mensajeDelServer);
                                        } else {
                                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                                        }
                                        mostrarToast(Utilidades.mensajeDelServer );

                                    }
                                }else{
                                    mostrarToast("Tienen que estar rellenos el campo contraseña y tipo de usuario" );
                                }


                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aquí puedes poner código si el usuario decide no realizar la acción
                        Toast.makeText(getApplicationContext(), "Acción cancelada", Toast.LENGTH_SHORT).show();
                    }
                });

        // Mostrar el cuadro de diálogo
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}