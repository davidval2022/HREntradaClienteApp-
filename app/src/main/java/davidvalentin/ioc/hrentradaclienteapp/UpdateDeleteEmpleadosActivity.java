package davidvalentin.ioc.hrentradaclienteapp;

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
import java.net.Socket;
import java.util.List;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;
import modelo.Empleados;
import modelo.Empresa;

/**
 * Activity asociada a la modificacion de  empleados existentes. En la parte gráfica tenemos un formulario
 * donde introduciremos las modificaciones y luego a traves de esta activity seran tratados y enviados
 * al server
 */

public class UpdateDeleteEmpleadosActivity extends AppCompatActivity {

    private Bundle empleadoRecibido;//recibidos el bundle desde SelectEmpresaAsyn
    private Empleados empleado;//la utilizamos com variable empleado que mostramos/modificamos

    private Socket socket;
    private String nombreTabla = "0";//empleados es 0
    private String crud = "2";//update es el codigo crud 2
    private String orden = "0";//orden no lo utilizamos por lo tanto es siempres será 0
    private String dniOriginal = "";//dni original, lo utilizaremos en el envio de datos, campo base en consulta

    //campos de texto de la activity
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
        setContentView(R.layout.activity_update_delete_empleados);

        socket = Utilidades.socketManager.getSocket();




        editTextDniEmpl = findViewById(R.id.editTextDniEmplUpdateDel);
        editTextNomEmpl = findViewById(R.id.editTextNomEmplUpdateDel);
        editTextApellido = findViewById(R.id.editTextApellidoUpdateDel);
        editTextNomEmpresaEmpl = findViewById(R.id.editTextNomEmpresaEmplUpdateDel);
        editTextDepartament = findViewById(R.id.editTextDepartamentUpdateDel);
        editTextCodicardEmpl = findViewById(R.id.editTextCodicardEmplUpdateDel);
        editTextMail = findViewById(R.id.editTextMailUpdateDel);
        editTextTelefonoEmpl = findViewById(R.id.editTextTelefonoEmplUpdateDel);


        //parte para rellenar los campos con los datos recibidos de SelectEmpleadosAsyn
        empleadoRecibido = getIntent().getExtras();
        empleado = null;
        if(empleadoRecibido != null){
            //guardamos en la variable empresa la empresa recibida
            empleado=(Empleados) empleadoRecibido.getSerializable("empleado");
            //nos quedamos con el nombre original, para luego saber a que registro hacemos el update
            dniOriginal = empleado.getDni();

            editTextDniEmpl.setText(empleado.getDni());
            editTextNomEmpl.setText(empleado.getNom());
            editTextApellido.setText(empleado.getApellido());
            editTextNomEmpresaEmpl.setText(empleado.getNomempresa());
            editTextDepartament.setText(empleado.getDepartament());
            editTextCodicardEmpl.setText(empleado.getCodicard());
            editTextMail.setText(empleado.getMail());
            editTextTelefonoEmpl.setText(empleado.getTelephon());
        }

    }

    /**
     * Metodo asociado al boton 'guardar empleado'. Mediante este método enviamos los datos al
     * server para actualizar un  empleado. En este caso y a diferencia de cuando haciamos los
     * select, al no cambiar la pantalla en este justo momento no necesitaremos clases de tipo
     * Asyntask, o hacerlo en un hilo diferente del principal que maneja la UI.
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void actualizarEmpleadoBD(View view) {

        try {
            //Log.d("Enviado", "Aqui no enviamos nada pero estamos en actualizarEmpleadoBD");
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
                    String palabra = Utilidades.codigo+","+crud+","+nombreTabla+",dniNuevo,"+dni+",nomNuevo,"+nom+",apellidoNuevo,"+apellido+",nomempresaNuevo,"+empresa+",departamentNuevo,"+
                            departament+",codicardNuevo,"+codicard+",mailNuevo,"+mail+",telephonNuevo,"+telefono+",dni,"+dniOriginal+","+orden;
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
                            Utilidades.mensajeDelServer = "Se ha modificado correctamente el empleado";
                        } else if (receivedData instanceof String) {
                            Utilidades.mensajeDelServer = (String) receivedData;
                            Log.d("Recibido",Utilidades.mensajeDelServer);
                        } else {
                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                        }
                        mostrarToast(Utilidades.mensajeDelServer );

                    }
                }else{
                    mostrarToast("Tienes que poner el dni, nombre,apellido y nombre de empresa" );
                }


            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Método asociado al boton 'resetear campos' para borrar todos los datos introducidos.
     * @param view representa la vista con la que se está interactuando, no utilizado aquí
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
        Toast.makeText(this, ""+mensaje, Toast.LENGTH_LONG).show();


    }

    /**
     * Metodo para eliminar un empleado
     * @param view representa la vista con la que se está interactuando
     */
    public void eliminarEmpleadoBD(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar acción")
                .setMessage("¿Estás seguro de que quieres eliminar este empleado?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            //Log.d("Enviado", "Aqui no enviamos nada pero estamos en actualizarEmpleadoBD");
                            if (socket != null && socket.isConnected()) {
                                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                ObjectInputStream perEnt;

                                String codigo = "0";
                                String dni = "0";

                                dni = editTextDniEmpl.getText().toString();
                                String crud = "3";

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
                                            //Utilidades.listaEmpleados = (ArrayList) receivedData;
                                            Utilidades.mensajeDelServer = "Se ha eliminado correctamente el empleado";
                                        } else if (receivedData instanceof String) {
                                            Utilidades.mensajeDelServer = (String) receivedData;
                                            Log.d("Recibido",Utilidades.mensajeDelServer);
                                        } else {
                                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                                        }
                                        mostrarToast(Utilidades.mensajeDelServer );

                                    }
                                }else{
                                    mostrarToast("Tienes que poner el dni, nombre,apellido y nombre de empresa" );
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