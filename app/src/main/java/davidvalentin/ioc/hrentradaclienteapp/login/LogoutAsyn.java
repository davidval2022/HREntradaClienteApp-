package davidvalentin.ioc.hrentradaclienteapp.login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author David Valentin Mateo
 * La clase `LogoutAsyn` es una subclase de `AsyncTask` que se utiliza para manejar la desconexión
 * de un socket del servidor en segundo plano. Esta clase realiza la desconexión y muestra un mensaje
 * del servidor a través de un `Toast` después de completar la tarea.
 */

public class LogoutAsyn extends AsyncTask<Void, Void, String> {
    // Gestor de sockets para manejar la conexión con el servidor
    private SocketManager socketManager;
    // Contexto de la aplicación
    private Context context;
    // Socket utilizado para la conexión con el servidor
    private Socket socket;


    /**
     * Constructor de la clase `LogoutAsyn`.
     *
     * @param socketManager Instancia de `SocketManager` utilizada para gestionar la conexión del socket.
     * @param context       Contexto de la aplicación utilizado para mostrar mensajes del servidor.
     */
    public LogoutAsyn(SocketManager socketManager, Context context) {
        this.socketManager = socketManager;
        this.context = context;

    }

    /**
     * Método que se ejecuta en segundo plano para realizar la desconexión del socket del servidor.
     *
     * @param params Parámetros de entrada (no utilizados en esta implementación).
     * @return Mensaje del servidor después de la desconexión.
     */
    @Override
    protected String doInBackground(Void... params) {

        try {
            // Obtiene el socket del SocketManager
            socket = socketManager.getSocket();

            // Verifica si el socket no es nulo y está conectado
            if (socket != null && socket.isConnected()) {
                Log.d("Correcto","El socket está conectado en logoutasyn");
                // Configura los flujos de entrada y salida del socket
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                //String mensajeServer = lector.readLine();   //leemos el mensaje del del server
                // Define un mensaje del servidor (se utiliza un valor predeterminado en este caso)
                String mensajeServer = "Desconectando";
                Log.d("Correcto","Mensaje del Server en logout: "+"No hay mensaje");

                // Envia un mensaje al servidor indicando la desconexión
                String palabra = "exit";
                //ahora escribimos en servidor , enviandole el login
                escriptor.write(palabra);
                escriptor.newLine();
                escriptor.flush();
                // Cierra los flujos y el socket si el mensaje es "exit"
                if(palabra.equalsIgnoreCase("exit")){
                    lector.close();
                    escriptor.close();
                    socket.close();
                }
                // Devuelve el mensaje del servidor después de la desconexión
                return mensajeServer;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método que se ejecuta en el hilo principal después de que se completa la tarea en segundo plano.
     *
     * @param s Mensaje del servidor después de la desconexión.
     */
    @Override
    protected void onPostExecute(String s) {

        Toast.makeText(context, "Mensaje del server antes del logout es: "+s, Toast.LENGTH_SHORT).show();
    }
}
