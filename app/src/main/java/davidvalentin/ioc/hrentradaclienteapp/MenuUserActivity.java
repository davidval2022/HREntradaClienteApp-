package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.LogoutAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SelectEmpleadosAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

public class MenuUserActivity extends AppCompatActivity {
    private TextView mensajeNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);

        mensajeNombre = findViewById(R.id.textViewMensajeConNombre);
        mensajeNombre.setText("BIENVENIDO "+Utilidades.nombreUser.toUpperCase());


    }

    public void logout(View view) {
        try{
            LogoutAsyn logout = new LogoutAsyn(Utilidades.socketManager,getApplicationContext());
            logout.execute();
            //vuelvo a login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            Log.d("Correcto","Voy a LogoutAsyn ");
        }catch(Exception e){
            Log.d("Error","Errores en logout: "+e);
        }
    }

    public void empleados(View view){
        Intent intent = new Intent(this, EmpleadosActivity.class);
        startActivity(intent);
    }

    public void empresas(View view) {
        Intent intent = new Intent(this,  EmpresasActivity.class);
        startActivity(intent);
    }
    public void jornadas(View view){
        Intent intent = new Intent(this,  JornadasActivity.class);
        startActivity(intent);
    }
}