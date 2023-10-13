package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import davidvalentin.ioc.hrentradaclienteapp.MainActivity;
import model.Resultado;

public class Conexion extends AsyncTask<String, Void, String> {
    private SocketManager socketManager;
    private String usuario;
    private String pass;
    private Context context;


    public Conexion(SocketManager socketManager, String usuario, String pass, Context context) {
        this.socketManager = socketManager;
        this.usuario = usuario;
        this.pass = pass;
        this.context = context;
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
                String palabra = usuario+":"+pass;
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
                Utilidades.codigo = codigo;
            }
        } else {
            // Maneja el caso en el que no se pudo obtener el resultado del servidor
            Toast.makeText(context, "Error de comunicación con el servidor", Toast.LENGTH_SHORT).show();
        }

    }



}
