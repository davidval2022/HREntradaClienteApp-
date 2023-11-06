package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import java.net.Socket;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.SocketManager;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

public class EmpleadosInsertActivity extends AppCompatActivity {

    private SocketManager socketManager;
    private String nombreTabla;
    private String columna;
    private String filtro;
    private String orden;
    private Socket socket;
    private String crud;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados_insert);


    }

    public void volver(View view) {
        Intent intent = new Intent(this, EmpresasActivity.class);
        startActivity(intent);

    }


}