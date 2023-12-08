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
import java.util.ArrayList;
import java.util.List;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;
import modelo.Users;

/**
 * Activity creada para que los usuarios (admin o users) cambien sus contraseñas
 * Los Administradores pueden también cambiar las contraseñas de cualquier usuario
 * desde la ventana de Actualizar/Eliminar users.
 */
public class CambioPassActivity extends AppCompatActivity {

    private Socket socket;
    private String nombreTabla = "1";//users es 1
    private String crud = "2";//update es el codigo crud 2
    private String orden = "0";//orden no lo utilizamos por lo tanto es siempres será 0


    private EditText editTextPassUser;
    private EditText editTextPassUserComprobracion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_pass);

        socket = Utilidades.socketManager.getSocket();

        editTextPassUser = findViewById(R.id.editTextPassNueva);
        editTextPassUserComprobracion = findViewById(R.id.editTextPassNuevaComprobacion);


    }

    /**
     * Con este método se envia la información al server para cambiar la contraseña.
     * Los datos los recogemos de los campos de texto y si coinciden se envían al
     * server para actualizar la pass.
     * @param view
     */
    public void cambiarPass(View view) {
        String pass = editTextPassUser.getText().toString();
        String comprobacion = editTextPassUserComprobracion.getText().toString();

        if(!pass.equals("") && !(pass == null) && pass.equalsIgnoreCase(comprobacion)){
            try {

                if (socket != null && socket.isConnected()) {
                    BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    ObjectInputStream perEnt;
                    String palabra = "";


                    //Como ya tenemos todos los datos que necesitamos enviar, podemos hacer el cambio de contraseña
                    palabra = Utilidades.codigo+","+crud+","+nombreTabla+",passNuevo,"+pass+",numtipeNuevo,"+Utilidades.tipoUser+",login,"+Utilidades.nombreUser+","+orden;
                    escriptor.write(palabra);
                    escriptor.newLine();
                    escriptor.flush();
                    Log.d("Enviado", "Le enviamos esto al server: "+palabra);

                    perEnt = new ObjectInputStream(socket.getInputStream());
                    Object receivedData = perEnt.readObject();

                    if (receivedData instanceof List) {
                        Utilidades.mensajeDelServer = "Se ha modificado correctamente la contraseña";
                    } else if (receivedData instanceof String) {
                        Utilidades.mensajeDelServer = (String) receivedData;
                        Log.d("Recibido",Utilidades.mensajeDelServer);
                    } else {
                        Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                    }
                    mostrarToast(Utilidades.mensajeDelServer );

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            mostrarToast("Las contraseñas no pueden estar vacías,comprueba que sean iguales en los dos campos");
        }



    }

    /**
     * Metodo asociado al botón 'volver'. Con este método somo redirigidos a
     * bien al MenuAdminActivity,o bien al menú MenuUserActivity dependiendo de si el usuario
     * es de tipo 1 o 0
     * @param view
     */
    public void volver(View view) {
        if(Utilidades.tipoUser == 0){
            Intent intent = new Intent(this, MenuAdminActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, MenuUserActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Metodo que lanza un mensaje de Toast
     * @param mensaje es el mensaje que mostrará el Toast
     */
    public  void mostrarToast(String mensaje){
        Toast.makeText(this, ""+mensaje, Toast.LENGTH_LONG).show();
    }

}