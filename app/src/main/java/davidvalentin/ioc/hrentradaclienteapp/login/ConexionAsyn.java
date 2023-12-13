package davidvalentin.ioc.hrentradaclienteapp.login;

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

import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

/**
 * @author David Valentin Mateo
 * Clase ConexionAsyn: Realiza una conexión asíncrona con el servidor utilizando la clase AsyncTask.
 * Esta clase maneja la comunicación asíncrona con el servidor para realizar la autenticación de usuario.
 * Extiende AsyncTask, permitiendo la ejecución de operaciones en segundo plano sin bloquear la interfaz de usuario.
 */

public class ConexionAsyn extends AsyncTask<String, Void, String> {
    // Gestor de sockets para manejar la conexión con el servidor
    private SocketManager socketManager;
    // Credenciales de usuario
    private String usuario;
    private String pass;
    // Contexto de la aplicación
    private Context context;
    // Elementos de la interfaz de usuario que se actualizarán durante la conexión
    private TextView mensaje;
    private Button btnMenu;
    private Button btnEnviar;


    /**
     * Constructor de la clase ConexionAsyn.
     *
     * @param socketManager Gestor de sockets para manejar la conexión con el servidor.
     * @param usuario       Nombre de usuario para la autenticación.
     * @param pass          Contraseña del usuario para la autenticación.
     * @param context       Contexto de la aplicación.
     * @param mensajeLogin  TextView que mostrará mensajes relacionados con la conexión.
     * @param btnMenu       Botón de menú que se habilitará en caso de autenticación exitosa.
     * @param btnEnviar     Botón de enviar que se deshabilitará durante la conexión.
     */

    public ConexionAsyn(SocketManager socketManager, String usuario, String pass, Context context, TextView mensajeLogin, Button btnMenu, Button btnEnviar) {

        this.socketManager = socketManager;
        this.usuario = usuario;
        this.pass = pass;
        this.context = context;
        this.mensaje = mensajeLogin;
        this.btnMenu = btnMenu;
        this.btnEnviar = btnEnviar;
    }


    /**
     * Método ejecutado en segundo plano para realizar la conexión y autenticación con el servidor.
     *
     * @param params Parámetros de entrada para la conexión.
     * @return Código de autenticación recibido del servidor.
     */

    @Override
    protected String doInBackground(String... params) {

        try {
            // Obtiene el socket del gestor de sockets
            Socket socket = socketManager.getSocket();

            // Verifica la conexión del socket
            if (socket != null && socket.isConnected()) {
                // Configura los flujos de entrada y salida para la comunicación con el servidor
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;
                // Inicializa el código de autenticación
                String codigo = "0";

                String mensajeServer = lector.readLine();   //leemos el mensaje de bienvenidoa del server
                String palabra = usuario+","+pass;
                //ahora escribimos en servidor , enviandole el login
                escriptor.write(palabra);
                escriptor.newLine();
                escriptor.flush();
                // Maneja la desconexión si el mensaje es "exit"
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

    /**
     * Método ejecutado después de la finalización de la conexión en segundo plano.
     *
     * @param codigo Código de autenticación recibido del servidor.
     */
    @Override
    protected void onPostExecute(String codigo) {
        // Actualiza la interfaz de usuario si es necesario
        if (codigo != null) {
            if (codigo.equals("-1")) {
                // El inicio de sesión falló, muestra un mensaje de error
                Toast.makeText(context, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show();
            }else if(codigo.equals("-2")){
                Toast.makeText(context, "Inicio de sesión fallido,. El usuario ya está registrado", Toast.LENGTH_SHORT).show();
            }
            else {
                // El inicio de sesión fue exitoso, muestra un mensaje de éxito
                Toast.makeText(context, "Inicio de sesión exitoso: "+ codigo, Toast.LENGTH_SHORT).show();
                Log.d("Correcto","Mensaje del server en conexionasyn: "+ codigo);
                Utilidades.codigo = codigo;
                btnEnviar.setEnabled(false);


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
