package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import davidvalentin.ioc.hrentradaclienteapp.R;
import modelo.Empleados;

public class SelectEmpleadosAsyn extends AsyncTask<String, Void, ArrayList<Empleados>> {


    private SocketManager socketManager;
    private Context context;
    private String nombreTabla;
    private String columna;
    private String filtro;
    private String orden;
    private Socket socket;
    private String crud;
    private  RecyclerView recycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //atributos por si buscamos por nombre y apellido
    private String nombre;
    private String apellido;





    public SelectEmpleadosAsyn(SocketManager socketManager,Context context,String crud, String nombreTabla,String columna,String filtro,String orden, RecyclerView recycler
    ,RecyclerView.Adapter mAdapter,RecyclerView.LayoutManager layoutManager) {
        this.socketManager = socketManager;
        this.context = context;
        this.nombreTabla = nombreTabla;
        this.columna = columna;
        this.filtro = filtro;
        this.orden = orden;
        this.crud = crud;
        this.recycler = recycler;
        this.mAdapter = mAdapter;
        this.layoutManager = layoutManager;


    }


    @Override
    protected ArrayList<Empleados> doInBackground(String... params) {

        try {
            socket = socketManager.getSocket();



            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                String codigo = "0";

                //String mensajeServer = lector.readLine();   //leemos el mensaje de bienvenidoa del server
                String palabra = Utilidades.codigo+","+crud+","+nombreTabla+","+columna+","+filtro+","+orden;
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
                    Utilidades.listaEmpleados = (ArrayList) perEnt.readObject();



                }
                return Utilidades.listaEmpleados;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null ;
    }

    @Override
    protected void onPostExecute(ArrayList<Empleados> result) {
        //NOTA: ES AQUÍ DONDE HAY QUE LLENAR EL RECYCLERVIEW DE EMPLEADOSACTIVITY
        super.onPostExecute(result);
        //us


        // Usar un administrador para el RecyclerView
        layoutManager = new LinearLayoutManager(context);
        recycler.setLayoutManager(layoutManager);
        // Crear un adaptador y establecerlo en el RecyclerView
        mAdapter = new AdaptadorEmpleados(Utilidades.listaEmpleados); // Asegúrate de reemplazar MyAdapter con tu propio adaptador
        recycler.setAdapter(mAdapter);


    }









}
