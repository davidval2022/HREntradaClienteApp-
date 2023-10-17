package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SelectEmpleadosAsyn extends AsyncTask<String, Void, Void> {
    private SocketManager socketManager;
    private Context context;
    private String nombreTabla;
    private String columna;
    private String filtro;
    private String orden;
    private Socket socket;
    private String crud;




    public SelectEmpleadosAsyn(SocketManager socketManager,Context context,String crud, String nombreTabla,String columna,String filtro,String orden) {
        this.socketManager = socketManager;
        this.context = context;
        this.nombreTabla = nombreTabla;
        this.columna = columna;
        this.filtro = filtro;
        this.orden = orden;
        this.crud = crud;


    }


    @Override
    protected Void doInBackground(String... params) {

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
               // return (ArrayList<Empleados>) listaEmpleados;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        //super.onPostExecute(unused);
        Log.d("Error", "Estamos en onPostExecute pero no ha pasado nada");
        if(Utilidades.listaEmpleados.size() == 0 ){
            Log.d("Error", "La cosa no ha ido bien el el onPostExecute, ni al recibir el arrayList");
        }
        for(int i=0;i<Utilidades.listaEmpleados.size();i++){
            //mensaje+="Nombre: " + listaEmpleados.get(i).getNom() + " Apellidos: " + listaEmpleados.get(i).getApellido()+ " DNI: "+listaEmpleados.get(i).getDni()+"\n";
            Log.d("Correcto","Nombre: " + Utilidades.listaEmpleados.get(i).getNom() + " Apellidos: " + Utilidades.listaEmpleados.get(i).getApellido()+ " DNI: "+Utilidades.listaEmpleados.get(i).getDni()+"\n");
        }
    }





}
