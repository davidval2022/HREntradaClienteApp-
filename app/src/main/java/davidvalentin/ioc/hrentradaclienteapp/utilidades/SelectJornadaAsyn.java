package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.content.Context;
import android.os.AsyncTask;
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


import modelo.Jornada;

public class SelectJornadaAsyn extends AsyncTask<String, Void, ArrayList<Jornada>> {


    private SocketManager socketManager;
    private Context context;
    private String nombreTabla;
    private String columna1;
    private String filtro1;
    private String columna2;
    private String filtro2;
    private String orden;
    private Socket socket;
    private String crud;
    private  RecyclerView recycler;
    private AdaptadorJornadas mAdapter;
    private RecyclerView.LayoutManager layoutManager;






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


    @Override
    protected ArrayList<Jornada> doInBackground(String... params) {

        try {
            socket = socketManager.getSocket();





            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                String codigo = "0";

                String palabra = Utilidades.codigo+","+crud+","+nombreTabla+","+columna1+","+filtro1+","+columna2+","+filtro2+","+orden;
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

    @Override
    protected void onPostExecute(ArrayList<Jornada> result) {

        super.onPostExecute(result);

        layoutManager = new LinearLayoutManager(context);
        recycler.setLayoutManager(layoutManager);
        // Crear un adaptador y establecerlo en el RecyclerView
        mAdapter = new AdaptadorJornadas(Utilidades.listaJornadas);

        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(),"Seleccion: "+Utilidades.listaJornadas.get(recycler.getChildAdapterPosition(v)).getDni(),Toast.LENGTH_SHORT).show();

            }
        });
        recycler.setAdapter(mAdapter);






    }




}
