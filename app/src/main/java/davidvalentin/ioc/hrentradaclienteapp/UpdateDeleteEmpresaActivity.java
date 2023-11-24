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
import modelo.Empresa;


public class UpdateDeleteEmpresaActivity extends AppCompatActivity {

    private Bundle empresaRecibida;//recibidos el bundle desde SelectEmpresaAsyn
    private Empresa empresa;//La utilizamos como variable empresa, que muestramos/modificamos

    private Socket socket;//socket
    private String nombreTabla = "2";//empresa es 2
    private String crud = "2";//update es el codigo crud 2
    private String orden = "0";//orden no lo utilizamos por lo tanto es siempres será 0
    private String nombreOriginal = "";//Nombre original, lo utilizaremos en el envio de datos

    //campos de texto de la activity
    private EditText editTextNomEmpresaUpdate;
    private EditText editTextAddressEmpresaUpdate;
    private EditText editTextTelefonoEmpresaUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_empresa);

        socket = Utilidades.socketManager.getSocket();

        editTextNomEmpresaUpdate = findViewById(R.id.editTextNomUpDelEmpresa);
        editTextAddressEmpresaUpdate = findViewById(R.id.editTextAddressUpDelEmpresa);
        editTextTelefonoEmpresaUpdate = findViewById(R.id.editTextTelefonoUpDelEmpresa);

        empresaRecibida = getIntent().getExtras();
        empresa = null;
        if(empresaRecibida != null){
            //guardamos en la variable empresa la empresa recibida
            empresa=(Empresa)empresaRecibida.getSerializable("empresa");
            //nos quedamos con el nombre original, para luego saber a que registro hacemos el update
            nombreOriginal = empresa.getNom();

            if(!empresa.getNom().equals("")){
                editTextNomEmpresaUpdate.setText(empresa.getNom());
            }
            if(!empresa.getTelephon().equals("")){
                editTextAddressEmpresaUpdate.setText(empresa.getAddress());
            }

            if(!empresa.getTelephon().equals("")){
                editTextTelefonoEmpresaUpdate.setText(empresa.getTelephon());
            }



        }
    }

    /**
     * Metodo asociado al boton 'guardar empresa'. Mediante este método enviamos los datos al
     * server para actualizar una  empresa. En este caso y a diferencia de cuando haciamos los
     * select, al no cambiar la pantalla en este justo momento no necesitaremos clases de tipo
     * Asyntask, o hacerlo en un hilo diferente del principal que maneja la UI.
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */

    public void actualizarEmpresa(View view) {
        try {

            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                String codigo = "0";
                String nombre = "0";
                String address = "0";
                String telephon = "0";

                nombre = editTextNomEmpresaUpdate.getText().toString();
                address = editTextAddressEmpresaUpdate.getText().toString();
                telephon = editTextTelefonoEmpresaUpdate.getText().toString();

                if(telephon.equals("") || telephon.equals(" ") || telephon.equals(null)){
                    telephon = "0";
                }

                if(!nombre.equals("") && !address.equals("")){
                    String palabra = Utilidades.codigo+","+crud+","+nombreTabla+",nomNuevo,"+nombre+",addressNuevo,"+address+",telephonNuevo,"+telephon+
                            ",nom,"+nombreOriginal+","+orden;
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
                            // Utilidades.listaEmpleados = (ArrayList) receivedData;
                            Utilidades.mensajeDelServer = "Se ha  modificado el registro correctamente";
                        } else if (receivedData instanceof String) {
                            Utilidades.mensajeDelServer = (String) receivedData;
                        } else {
                            Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                        }
                        mostrarToast(Utilidades.mensajeDelServer );

                    }
                }else{
                    mostrarToast("Molt malament, tienes que insertar el nombre y direccion de la empresa" );
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
        editTextNomEmpresaUpdate.setText("");
        editTextAddressEmpresaUpdate.setText("");
        editTextTelefonoEmpresaUpdate.setText("");
    }

    /**
     * Metodo asociado al botón 'volver'. Con este método somo redirigidos a
     * EmpleadosActivity
     */
    public void volver(View view) {
        Intent intent = new Intent(this, EmpresasActivity.class);
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
     * Metodo para eliminar una empresa
     * @param view
     */

    public void eliminarEmpresa(View view) {
    }
}