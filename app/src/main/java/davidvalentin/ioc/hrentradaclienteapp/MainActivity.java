package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
///
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.LoginConsulta;


public class MainActivity extends AppCompatActivity {

    EditText editIp;
    EditText editUsuario;
    EditText editPass;
    String ip = "127.0.0.1";
    String usuario = "0";
    String pass = "0";
    LoginConsulta lconsulta;
    String codigoGlobal = "0";
    Socket socket;
    ObjectOutputStream outObjeto;
    ObjectInputStream inObjeto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lconsulta = new LoginConsulta();
        editIp = findViewById(R.id.edit_ip);
        editUsuario = findViewById((R.id.edit_usuario));
        editPass = findViewById(R.id.edit_pass);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build());
        ///////////////////////////////////////////////////
        //ip = "192.168.1.12";
        //usuario= "gus";
        //pass = "123";
        /*
        try {
            // login(); //Realizar aquí tu proceso!
            lconsulta.setInfoDelServer("Pruebas de funcionamiento");
            mostrarToast(lconsulta.getInfoDelServer());
            try{
                ip = "192.168.1.12";
                usuario= "gus";
                pass = "123";
                socket = new Socket(ip, 8888);
                ObjectOutputStream outObjeto = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inObjeto = new ObjectInputStream(socket.getInputStream());
                lconsulta = (LoginConsulta) inObjeto.readObject();//recibimos mensaje 1 del server////////////////////////////////////////////////////////
                mostrarToast(lconsulta.getInfoDelServer());

                String codigo = lconsulta.getCodigo();

                //////
                // System.out.println(lconsulta.getInfoDelServer());
                if(codigo.equalsIgnoreCase("0")){//si no tenemos codigo enviamos un objeto con el usuario y contraseña para hacer login y que nos envien un codigo
                    lconsulta.setUsuario(usuario);
                    lconsulta.setContraseña(pass);
                    //ahora acabamos el objeto lconsulta y lo enviamos al server para que nos logee y nos envie un codigo
                    lconsulta.setTipoDeLogin("1");//como vamos a hacer que nos logee el tipo de login es 1
                    outObjeto.writeObject(lconsulta);//envio mensaje 1 al server////////////////////////////////////////////////////////
                    //ahora esperamos el codigo que nos debe de enviar si las credenciales son correctas.
                    //Para hacer esto  lo que hará será coger nuestro objeto y añadirle el codigo.
                    lconsulta =(LoginConsulta) inObjeto.readObject();//recibo mensaje 2 del server////////////////////////////////////////////////////////
                    //ahora vamos a leer  y guardar el codigo, si no nos envía ningun error, ya que si ya estamos registrados nos enviará un error
                    if(lconsulta.getError().equalsIgnoreCase("0")){
                        codigo = lconsulta.getCodigo();
                        codigoGlobal = codigo;
                        //ahora cerramos la conexion. La siguiente vez que conectemos ya enviaremos el codigo, no hará falta enviar usuario y contraseña
                        mostrarToast("Mensaje del server: \n"+lconsulta.getInfoDelServer()+ " El codigo adjudicado es: "+codigo);
                    }else{
                        //System.out.println("A ocurrido un error, el codigo del error es: "+lconsulta.getError());
                        //System.out.println(lconsulta.getInfoDelServer());
                    }



                }else{
                    //System.out.println("El codigo adjudicados es: "+codigo + "Así que tenemos que enviarlo en el objeto");

                }

                socket.close();

            }catch(IOException | ClassNotFoundException e){
                //mostrarToast("Error de entrada/salida");
            }




        } catch (Exception e) {
            Log.e("Error", "Exception: " + e.getMessage());
        }
        */

    /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                   // login(); //Realizar aquí tu proceso!
                    try{
                        //ip = "192.168.1.12";
                        //usuario= "gus";
                        //pass = "123";
                        socket = new Socket(ip, 8888);
                        outObjeto = new ObjectOutputStream(socket.getOutputStream());
                        inObjeto = new ObjectInputStream(socket.getInputStream());
                        lconsulta = (LoginConsulta) inObjeto.readObject();//recibimos mensaje 1 del server////////////////////////////////////////////////////////
                        mostrarToast(lconsulta.getInfoDelServer());

                        String codigo = lconsulta.getCodigo();

                        //////
                        // System.out.println(lconsulta.getInfoDelServer());
                        if(codigo.equalsIgnoreCase("0")){//si no tenemos codigo enviamos un objeto con el usuario y contraseña para hacer login y que nos envien un codigo
                            lconsulta.setUsuario(usuario);
                            lconsulta.setContraseña(pass);
                            //ahora acabamos el objeto lconsulta y lo enviamos al server para que nos logee y nos envie un codigo
                            lconsulta.setTipoDeLogin("1");//como vamos a hacer que nos logee el tipo de login es 1
                            outObjeto.writeObject(lconsulta);//envio mensaje 1 al server////////////////////////////////////////////////////////
                            //ahora esperamos el codigo que nos debe de enviar si las credenciales son correctas.
                            //Para hacer esto  lo que hará será coger nuestro objeto y añadirle el codigo.
                            lconsulta =(LoginConsulta) inObjeto.readObject();//recibo mensaje 2 del server////////////////////////////////////////////////////////
                            //ahora vamos a leer  y guardar el codigo, si no nos envía ningun error, ya que si ya estamos registrados nos enviará un error
                            if(lconsulta.getError().equalsIgnoreCase("0")){
                                codigo = lconsulta.getCodigo();
                                codigoGlobal = codigo;
                                //ahora cerramos la conexion. La siguiente vez que conectemos ya enviaremos el codigo, no hará falta enviar usuario y contraseña
                                mostrarToast("Mensaje del server: \n"+lconsulta.getInfoDelServer()+ " El codigo adjudicado es: "+codigo);
                            }else{
                                //System.out.println("A ocurrido un error, el codigo del error es: "+lconsulta.getError());
                                //System.out.println(lconsulta.getInfoDelServer());
                            }



                        }else{
                            //System.out.println("El codigo adjudicados es: "+codigo + "Así que tenemos que enviarlo en el objeto");

                        }

                        socket.close();

                    }catch(IOException | ClassNotFoundException e){
                        //mostrarToast("Error de entrada/salida");
                    }




                } catch (Exception e) {
                    Log.e("Error", "Exception: " + e.getMessage());
                }
            }
        });

*/





    }

    public void enviarDatosLogin(View view){
        ip = editIp.getText().toString();
        usuario = editUsuario.getText().toString();
        pass = editPass.getText().toString();

        mostrarToast(lconsulta.getInfoDelServer());
        login();
        /*
        ip = "192.168.1.12";
        usuario= "gus";
        pass = "123";
        mostrarToast("ip:"+ip + " usuario: "+usuario+ " pass: "+pass);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        login();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        getBaseContext(),
                                        "¡Funcionando el login!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        ).start();
        */






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


    public void mostrarToast(String mensaje){
        Toast.makeText(this, "Mensaje: "+mensaje, Toast.LENGTH_SHORT).show();

    }

    public void login(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // login(); //Realizar aquí tu proceso!
                    try{
                        //ip = "192.168.1.12";
                        //usuario= "gus";
                        //pass = "123";
                        socket = new Socket(ip, 8888);
                        outObjeto = new ObjectOutputStream(socket.getOutputStream());
                        inObjeto = new ObjectInputStream(socket.getInputStream());
                        lconsulta = (LoginConsulta) inObjeto.readObject();//recibimos mensaje 1 del server////////////////////////////////////////////////////////
                        mostrarToast(lconsulta.getInfoDelServer());

                        String codigo = lconsulta.getCodigo();

                        //////
                        // System.out.println(lconsulta.getInfoDelServer());
                        if(codigo.equalsIgnoreCase("0")){//si no tenemos codigo enviamos un objeto con el usuario y contraseña para hacer login y que nos envien un codigo
                            lconsulta.setUsuario(usuario);
                            lconsulta.setContraseña(pass);
                            //ahora acabamos el objeto lconsulta y lo enviamos al server para que nos logee y nos envie un codigo
                            lconsulta.setTipoDeLogin("1");//como vamos a hacer que nos logee el tipo de login es 1
                            outObjeto.writeObject(lconsulta);//envio mensaje 1 al server////////////////////////////////////////////////////////
                            //ahora esperamos el codigo que nos debe de enviar si las credenciales son correctas.
                            //Para hacer esto  lo que hará será coger nuestro objeto y añadirle el codigo.
                            lconsulta =(LoginConsulta) inObjeto.readObject();//recibo mensaje 2 del server////////////////////////////////////////////////////////
                            //ahora vamos a leer  y guardar el codigo, si no nos envía ningun error, ya que si ya estamos registrados nos enviará un error
                            if(lconsulta.getError().equalsIgnoreCase("0")){
                                codigo = lconsulta.getCodigo();
                                codigoGlobal = codigo;
                                //ahora cerramos la conexion. La siguiente vez que conectemos ya enviaremos el codigo, no hará falta enviar usuario y contraseña
                                mostrarToast("Mensaje del server: \n"+lconsulta.getInfoDelServer()+ " El codigo adjudicado es: "+codigo);
                            }else{
                                //System.out.println("A ocurrido un error, el codigo del error es: "+lconsulta.getError());
                                //System.out.println(lconsulta.getInfoDelServer());
                            }



                        }else{
                            //System.out.println("El codigo adjudicados es: "+codigo + "Así que tenemos que enviarlo en el objeto");

                        }

                        socket.close();

                    }catch(IOException | ClassNotFoundException e){
                        //mostrarToast("Error de entrada/salida");
                    }




                } catch (Exception e) {
                    Log.e("Error", "Exception: " + e.getMessage());
                }
            }
        });


    }



}