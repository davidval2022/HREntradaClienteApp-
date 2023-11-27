package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import davidvalentin.ioc.hrentradaclienteapp.UpdateDeleteEmpleadosActivity;
import davidvalentin.ioc.hrentradaclienteapp.UpdateDeleteJornadaActivity;
import modelo.Empleados;
import modelo.Jornada;

/**
 * La clase `SelectJornadaAsyn` es una subclase de `AsyncTask` diseñada para realizar operaciones
 * de consulta en segundo plano en un servidor utilizando sockets. Esta clase maneja la obtención de datos
 * desde el servidor y actualiza un `RecyclerView` con la lista de jornadas obtenida.
 *
 * @param \<String> Tipo de parámetro de entrada para el método `doInBackground`, que representa la operación CRUD.
 * @param \<Void> Tipo de parámetro de progreso para el método `onProgressUpdate` (no utilizado en esta implementación).
 * @param \<ArrayList<Jornada>> Tipo de resultado devuelto por el método `doInBackground` y pasado al método `onPostExecute`,
 *                               que representa la lista de jornadas obtenida del servidor.
 */
public class SelectJornadaAsyn extends AsyncTask<String, Void, ArrayList<Jornada>> {

    // Gestor de sockets para manejar la conexión con el servidor
    private SocketManager socketManager;

    // Contexto de la aplicación
    private Context context;

    // Detalles de la consulta al servidor
    private String nombreTabla;
    private String columna1;
    private String filtro1;
    private String columna2;
    private String filtro2;
    private String orden;
    private String crud;

    // Socket utilizado para la conexión con el servidor
    private Socket socket;

    // Componentes de la interfaz de usuario
    private  RecyclerView recycler;
    private AdaptadorJornadas mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //variable que enviaremos en un bundle a la activity UpdateDeleteEmpleados
    private Jornada jornada;




    /**
     * Constructor de la clase `SelectJornadaAsyn`.
     *
     * @param socketManager Instancia de `SocketManager` utilizada para gestionar la conexión del socket.
     * @param context       Contexto de la aplicación utilizado para interactuar con la interfaz de usuario.
     * @param crud          Operación CRUD a realizar en el servidor (Create, Read, Update, Delete).
     * @param nombreTabla   Nombre de la tabla en la base de datos del servidor.
     * @param columna1      Nombre de la primera columna a utilizar en la consulta.
     * @param filtro1       Filtro para la primera columna en la consulta.
     * @param columna2      Nombre de la segunda columna a utilizar en la consulta.
     * @param filtro2       Filtro para la segunda columna en la consulta.
     * @param orden         Orden de los resultados de la consulta.
     * @param recycler      Instancia de `RecyclerView` para mostrar la lista de jornadas.
     * @param mAdapter      Adaptador para el `RecyclerView`.
     * @param layoutManager Administrador de diseño para el `RecyclerView`.
     */

    public SelectJornadaAsyn(SocketManager socketManager, Context context, String crud, String nombreTabla, String columna1, String filtro1, String columna2, String filtro2, String orden, RecyclerView recycler
    , AdaptadorJornadas mAdapter, RecyclerView.LayoutManager layoutManager) {
        this.socketManager = socketManager;
        this.context = context;
        this.nombreTabla = nombreTabla;
        this.columna1 = columna1;
        this.filtro1 = filtro1;
        this.columna2 = columna2;
        this.filtro2 = filtro2;
        this.orden = orden;
        this.crud = crud;
        this.recycler = recycler;
        this.mAdapter = mAdapter;
        this.layoutManager = layoutManager;


    }


    /**
     * Método que se ejecuta en segundo plano para realizar la consulta al servidor y obtener la lista de jornadas.
     *
     * @param params Parámetros de entrada que representan la operación CRUD.
     * @return Lista de jornadas obtenida del servidor.
     */
    @Override
    protected ArrayList<Jornada> doInBackground(String... params) {

        try {
            // Obtiene el socket del SocketManager
            socket = socketManager.getSocket();

            // Verifica si el socket no es nulo y está conectado
            if (socket != null && socket.isConnected()) {
                // Configura los flujos de entrada y salida del socket
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                // Construye la cadena de consulta
                String palabra = Utilidades.codigo+","+crud+","+nombreTabla+","+columna1+","+filtro1+","+columna2+","+filtro2+","+orden;
                //ahora escribimos en servidor , enviandole el login
                escriptor.write(palabra);
                escriptor.newLine();
                escriptor.flush();
                Log.d("Enviado", "Le enviamos esto al server: "+palabra);

                // Verifica si la cadena es "exit" y cierra los flujos y el socket
                if(palabra.equalsIgnoreCase("exit")){
                    lector.close();
                    escriptor.close();
                    socket.close();
                }else{
                    // Lee los datos del servidor y actualiza la lista de jornadas
                    perEnt = new ObjectInputStream(socket.getInputStream());
                    Object receivedData = perEnt.readObject();


                    if (receivedData instanceof ArrayList) {
                        Utilidades.listaJornadas = (ArrayList) receivedData;
                    } else if (receivedData instanceof String) {
                        Utilidades.mensajeDelServer = (String) receivedData;
                    } else {
                        Utilidades.mensajeDelServer ="Datos inesperados recibidos del servidor";
                    }
                }
                return Utilidades.listaJornadas;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null ;
    }


    /**
     * Método que se ejecuta en el hilo principal después de que se completa la tarea en segundo plano.
     *
     * @param result Lista de jornadas obtenida del servidor.
     */
    @Override
    protected void onPostExecute(ArrayList<Jornada> result) {
        // Configura el RecyclerView con la lista de jornadas
        super.onPostExecute(result);

        layoutManager = new LinearLayoutManager(context);
        recycler.setLayoutManager(layoutManager);

        // Crea un adaptador y lo establece en el RecyclerView
        mAdapter = new AdaptadorJornadas(Utilidades.listaJornadas);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(),"Seleccion: "+Utilidades.listaJornadas.get(recycler.getChildAdapterPosition(v)).getDni(),Toast.LENGTH_SHORT).show();
                jornada = Utilidades.listaJornadas.get(recycler.getChildAdapterPosition(v));
                Intent intent=new Intent(context, UpdateDeleteJornadaActivity.class);
                // Agregar el flag FLAG_ACTIVITY_NEW_TASK para que me deje enviar los datos a una
                //activity desde una clase que no es una activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("jornada",jornada);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        recycler.setAdapter(mAdapter);

        // Muestra un Toast si hay un mensaje del servidor
        if(!Utilidades.mensajeDelServer.equals("")){
            mostrarToast(context.getApplicationContext(),Utilidades.mensajeDelServer );
        }
    }

    /**
     * Método estático para mostrar un Toast en la aplicación.
     *
     * @param context Contexto de la aplicación.
     * @param mensaje Mensaje a mostrar en el Toast.
     */
    public static void mostrarToast(Context context, String mensaje){
        Toast.makeText(context, ""+mensaje, Toast.LENGTH_SHORT).show();
    }




}
