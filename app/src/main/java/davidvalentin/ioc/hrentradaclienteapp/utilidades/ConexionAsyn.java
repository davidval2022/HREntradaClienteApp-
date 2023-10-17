package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ConexionAsyn extends AsyncTask<String, Void, String> {
    private SocketManager socketManager;
    private String usuario;
    private String pass;
    private Context context;
    private TextView mensaje;
    private Button btnMenu;


    public ConexionAsyn(SocketManager socketManager, String usuario, String pass, Context context, TextView mensajeLogin, Button btnMenu) {
        this.socketManager = socketManager;
        this.usuario = usuario;
        this.pass = pass;
        this.context = context;
        this.mensaje = mensajeLogin;
        this.btnMenu = btnMenu;
    }




    @Override
    protected String doInBackground(String... params) {

        try {
            Socket socket = socketManager.getSocket();


            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;
                String codigo = "0";

                String mensajeServer = lector.readLine();   //leemos el mensaje de bienvenidoa del server
                String palabra = usuario+","+pass;
                //ahora escribimos en servidor , enviandole el login
                escriptor.write(palabra);
                escriptor.newLine();
                escriptor.flush();

                if(palabra.equalsIgnoreCase("exit")){
                    lector.close();
                    escriptor.close();
                    socket.close();
                }else{
                    //leemos la respuesta, nos enviará un codigo
                    mensajeServer = lector.readLine();   //leemos ya la respuesta del server,    nos envia un código
                    codigo = mensajeServer;
                    //tambien vamos a guardar el nombre de usuario para utilizarlo en algunos sitios
                    Utilidades.nombreUser = usuario;
                }
                return codigo;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String codigo) {
        // Actualiza la interfaz de usuario si es necesario
        if (codigo != null) {
            if (codigo.equals("-1")) {
                // El inicio de sesión falló, muestra un mensaje de error
                Toast.makeText(context, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show();
            } else {
                // El inicio de sesión fue exitoso, muestra un mensaje de éxito
                Toast.makeText(context, "Inicio de sesión exitoso: "+ codigo, Toast.LENGTH_SHORT).show();
                Log.d("Correcto","Mensaje del server en conexionasyn: "+ codigo);
                Utilidades.codigo = codigo;

            //comprobar el tipo de usuario
            if(codigo.charAt(0) == 'A'){
                //System.out.println("El server indica que eres administrador");
                Utilidades.tipoUser = 0;
                mensaje.setText("Estas registrado como Admin");
                btnMenu.setEnabled(true);

            }else if(codigo.charAt(0) == 'U'){
                //System.out.println("El server indica que eres usuario normal");
                Utilidades.tipoUser = 1;
                mensaje.setText("Estas registrado como User");
                btnMenu.setEnabled(true);
            }

            }
        } else {
            // Maneja el caso en el que no se pudo obtener el resultado del servidor
            Toast.makeText(context, "Error de comunicación con el servidor", Toast.LENGTH_SHORT).show();
        }

    }



}
