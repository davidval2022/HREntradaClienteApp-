package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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
import modelo.Empleados;
import modelo.Jornada;

/**
 * Activity asociada al cierre  de una jornada existentes. En la parte gráfica tenemos un formulario
 * donde introduciremos las modificaciones y luego a traves de esta activity seran tratados y enviados
 * al server
 */
public class UpdateDeleteJornadaActivity extends AppCompatActivity {

    private Bundle jornadaRecibida;//recibidos el bundle desde SelectJornadaAsyn
    private Jornada jornada;//la utilizamos como variable jornada que cerramos, ya que la tenemos abierta

    private RadioButton opc_codi,opc_dni;
    private String opcionCampo = "";

    private Socket socket;
    private String nombreTabla = "3";//jornadas es 3
    private String crud = "2";//update es el codigo crud 2
    private String orden = "0";//orden no lo utilizamos por lo tanto es siempres será 0

    private EditText editTextDniJornada;//vale tanto para el codicard como para el dni

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_jornada);

        socket = Utilidades.socketManager.getSocket();


        opc_codi = (RadioButton) findViewById(R.id.idCodicardRadioCerrar);
        opc_dni = (RadioButton) findViewById(R.id.idDniRadioCerrar);
        editTextDniJornada = findViewById(R.id.editTextDniJornadaCerrar);
    }

    /**
     * Metodo asociado al boton 'cerrar  jornada'. Mediante este método enviamos los datos al
     * server para cerrar una jornada iniciada. En este caso y a diferencia de cuando haciamos los
     * select, al no cambiar la pantalla en este justo momento no necesitaremos clases de tipo
     * Asyntask, o hacerlo en un hilo diferente del principal que maneja la UI.
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void cerrarJornada(View view) {
        validar();
        //mostrarToast(opcionCampo);
        String codigo = "0";
        String dato = "0";
        String nombreCampo = "codicard";
        dato = editTextDniJornada.getText().toString();
        if(opcionCampo.equalsIgnoreCase("dni")){
            nombreCampo = "dni";
        }else if(opcionCampo.equalsIgnoreCase("dni")){
            nombreCampo = "codicard";
        }

        try {

            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                if(!nombreCampo.equals("") &&!dato.equalsIgnoreCase("")){
                    String palabra = Utilidades.codigo+","+crud+","+nombreTabla+","+nombreCampo+","+dato+","+orden;
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
                            Utilidades.mensajeDelServer = "Se ha cerrado la jornada correctamente";
                        } else if (receivedData instanceof String) {
                            Utilidades.mensajeDelServer = (String) receivedData;
                            Log.d("Recibido",Utilidades.mensajeDelServer);

                        } else {
                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                        }
                        mostrarToast(Utilidades.mensajeDelServer );

                    }
                }else{
                    mostrarToast("Molt malament, debes introducir el dni o codicard.. dependediendo de tu seleccion" );
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
    }

    /**
     * Metodo asociado al botón 'volver'. Con este método somo redirigidos a
     * JornadasActivity
     */
    public void volver(View view) {
        Intent intent = new Intent(this, JornadasActivity.class);
        startActivity(intent);
    }

    /**
     * Metodo que lanza un mensaje de Toast
     * @param mensaje es el mensaje que mostrará el Toast
     */
    public  void mostrarToast(String mensaje){
        Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();




    }

    /**
     * Este método es para saber a que campo nos estamos refiriendo al introducir el texto
     * en el EditText..
     * Es decir, que podemos crear el inicio de jornada del empleado introduciendo o bién su
     * dni o bien su codicard, y con este campo elegimos el tipo de dato que vamos a introducir.
     */
    public void validar(){
        if(opc_dni.isChecked()==true){
            opcionCampo = "dni";
        }
        if(opc_codi.isChecked()==true){
            opcionCampo = "codicard";
        }
    }
}