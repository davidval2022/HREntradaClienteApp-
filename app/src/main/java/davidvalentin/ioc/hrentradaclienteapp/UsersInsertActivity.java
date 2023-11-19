package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

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
import java.net.Socket;
import java.util.List;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;
/**
 * Activity asociada a la creación de nuevos users. En la parte gráfica tenemos un formulario
 * donde introduciremos los datos y luego a traves de esta activity seran tratados y enviados
 * al server
 */
public class UsersInsertActivity extends AppCompatActivity {

    private Socket socket;
    private String nombreTabla = "1";//users es 1
    private String crud = "1";//inserts es el codigo crud 1
    private String orden = "0";//orden no lo utilizamos por lo tanto es siempres será 0
    private EditText editTextLogin;
    private EditText editTextPassUser;
    private EditText editTextTipoUser;
    private EditText editTextDniUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_insert);

        socket = Utilidades.socketManager.getSocket();
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassUser = findViewById(R.id.editTextPassUser);
        editTextTipoUser = findViewById(R.id.editTextTipoUser);
        editTextDniUser = findViewById(R.id.editTextDniUser);


    }
    /**
     * Metodo asociado al boton 'guardar user'. Mediante este método enviamos los datos al
     * server para crear un nuevo user. En este caso y a diferencia de cuando haciamos los
     * select, al no cambiar la pantalla en este justo momento no necesitaremos clases de tipo
     * Asyntask, o hacerlo en un hilo diferente del principal que maneja la UI.
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void insertarUsuario(View view) {

        try {

            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                String codigo = "0";
                String login = "0";
                String pass = "0";
                String tipouser = "0";
                String dni = "0";

                login = editTextLogin.getText().toString();
                pass = editTextPassUser.getText().toString();
                tipouser = editTextTipoUser.getText().toString();
                dni = editTextDniUser.getText().toString();



                if(!login.equals("") && !pass.equals("") && !tipouser.equals("") && !dni.equals("")){
                    String palabra = Utilidades.codigo+","+crud+","+nombreTabla+",login,"+login+",pass,"+pass+",numtipe,"+Integer.parseInt(tipouser)+",dni,"+dni+","+orden;
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
                        Object receivedData = perEnt.readObject();

                        if (receivedData instanceof List) {
                            Utilidades.mensajeDelServer = "Se ha creado correctamente el registro";
                        } else if (receivedData instanceof String) {
                            Utilidades.mensajeDelServer = (String) receivedData;
                        } else {
                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                        }
                        mostrarToast(Utilidades.mensajeDelServer );

                    }
                }else{
                    mostrarToast("Molt malament, tienes que insertar todos los datos" );
                }

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método asociado al boton 'resetear campos' para borrar todos los datos introducidos.
     * @param view
     */
    public void reset(View view) {
        //falta por rellenar
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
}