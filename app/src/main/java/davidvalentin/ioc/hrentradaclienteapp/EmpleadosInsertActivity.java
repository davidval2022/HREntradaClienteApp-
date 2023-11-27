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

import davidvalentin.ioc.hrentradaclienteapp.utilidades.SocketManager;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

/**
 * Activity asociada a la creación de nuevos empleados. En la parte gráfica tenemos un formulario
 * donde introduciremos los datos y luego a traves de esta activity seran tratados y enviados
 * al server
 */

public class EmpleadosInsertActivity extends AppCompatActivity {

    private Socket socket;
    private String nombreTabla = "0";//empresa es 2
    private String crud = "1";//inserts es el codigo crud 1
    private String orden = "0";//orden no lo utilizamos por lo tanto es siempres será 0

    private EditText editTextDniEmpl;
    private EditText editTextNomEmpl;
    private EditText editTextApellido;
    private EditText editTextNomEmpresaEmpl;
    private EditText editTextDepartament;
    private EditText editTextCodicardEmpl;
    private EditText editTextMail;
    private EditText editTextTelefonoEmpl;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados_insert);

        socket = Utilidades.socketManager.getSocket();


        editTextDniEmpl = findViewById(R.id.editTextDniEmpl);
        editTextNomEmpl = findViewById(R.id.editTextNomEmpl);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextNomEmpresaEmpl = findViewById(R.id.editTextNomEmpresaEmpl);
        editTextDepartament = findViewById(R.id.editTextDepartament);
        editTextCodicardEmpl = findViewById(R.id.editTextCodicardEmpl);
        editTextMail = findViewById(R.id.editTextMail);
        editTextTelefonoEmpl = findViewById(R.id.editTextTelefonoEmpl);



    }
    /**
     * Metodo asociado al botón 'volver'. Con este método somo redirigidos a
     * EmpleadosActivity
     */
    public void volver(View view) {
        Intent intent = new Intent(this, EmpleadosActivity.class);
        startActivity(intent);

    }
    /**
     * Metodo que lanza un mensaje de Toast
     * @param mensaje es el mensaje que mostrará el Toast
     */
    public  void mostrarToast(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();


    }

    /**
     * Metodo asociado al boton 'guardar empleado'. Mediante este método enviamos los datos al
     * server para crear un nuevo empleado. En este caso y a diferencia de cuando haciamos los
     * select, al no cambiar la pantalla en este justo momento no necesitaremos clases de tipo
     * Asyntask, o hacerlo en un hilo diferente del principal que maneja la UI.
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void insertarEmpleadoBD(View view) {

        try {

            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                String codigo = "0";
                String dni = "0";
                String nom = "0";
                String apellido = "0";
                String empresa = "0";
                String departament = "0";
                String codicard = "0";
                String mail = "0";
                String telefono = "0";

                dni = editTextDniEmpl.getText().toString();
                nom = editTextNomEmpl.getText().toString();
                apellido = editTextApellido.getText().toString();
                empresa = editTextNomEmpresaEmpl.getText().toString();
                departament = editTextDepartament.getText().toString();
                codicard = editTextCodicardEmpl.getText().toString();
                mail = editTextMail.getText().toString();
                telefono = editTextTelefonoEmpl.getText().toString();



                if(departament.equals("") || departament.equals(" ") || departament.equals(null)){
                    departament = "0";
                }
                if(codicard.equals("") || codicard.equals(" ") || codicard.equals(null)){
                    codicard = "0";
                }
                if(mail.equals("") || mail.equals(" ") || mail.equals(null)){
                    mail = "0";
                }
                if(telefono.equals("") || telefono.equals(" ") || telefono.equals(null)){
                    telefono = "0";
                }


                if(!dni.equals("") && !empresa.equals("") && !nom.equals("") && !apellido.equals("")){
                    String palabra = Utilidades.codigo+","+crud+","+nombreTabla+",dni,"+dni+",nom,"+nom+",apellido,"+apellido+",nomempresa,"+empresa+",departament,"+
                            departament+",codicard,"+codicard+",mail,"+mail+",telephon,"+telefono+","+orden;
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
                             //Utilidades.listaEmpleados = (ArrayList) receivedData;
                            Utilidades.mensajeDelServer = "Se ha creado correctamente el registro";
                        } else if (receivedData instanceof String) {
                            Utilidades.mensajeDelServer = (String) receivedData;
                            Log.d("Recibido",Utilidades.mensajeDelServer);
                        } else {
                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                        }
                        mostrarToast(Utilidades.mensajeDelServer );

                    }
                }else{
                    mostrarToast("Tienes que insertar dni,nombre,apellido y nombre de empresa" );
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
        editTextDniEmpl.setText("");
        editTextNomEmpl.setText("");
        editTextApellido.setText("");
        editTextNomEmpresaEmpl.setText("");
        editTextDepartament.setText("");
        editTextCodicardEmpl.setText("");
        editTextMail.setText("");
        editTextTelefonoEmpl.setText("");
    }
}