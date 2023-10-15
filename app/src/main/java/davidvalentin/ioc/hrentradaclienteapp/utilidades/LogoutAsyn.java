package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import davidvalentin.ioc.hrentradaclienteapp.MenuAdminActivity;

public class LogoutAsyn extends AsyncTask<Void, Void, String> {
    private SocketManager socketManager;
    private Context context;
    private Socket socket;




    public LogoutAsyn(SocketManager socketManager, Context context) {
        this.socketManager = socketManager;
        this.context = context;

    }




    @Override
    protected String doInBackground(Void... params) {

        try {
            socket = socketManager.getSocket();
            if(socket.isConnected()){
                Log.d("Correcto","El socket está conectado en logoutasyn");
            }


            if (socket != null && socket.isConnected()) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter escriptor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream perEnt;

                //String mensajeServer = lector.readLine();   //leemos el mensaje del del server
                String mensajeServer = "Desconectando";
                Log.d("Correcto","Mensaje del Server en logout: "+"No hay mensaje");


                String palabra = "exit";
                //ahora escribimos en servidor , enviandole el login
                escriptor.write(palabra);
                escriptor.newLine();
                escriptor.flush();

                if(palabra.equalsIgnoreCase("exit")){
                    lector.close();
                    escriptor.close();
                    socket.close();
                }

                return mensajeServer;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        Toast.makeText(context, "Mensaje del server antes del logout es: "+s, Toast.LENGTH_SHORT).show();
    }
}
