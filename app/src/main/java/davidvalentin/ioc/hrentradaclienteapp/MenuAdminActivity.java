package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.LogoutAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SelectEmpleadosAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

public class MenuAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
    }

    public void logout(View view) {
        try{
            LogoutAsyn logout = new LogoutAsyn(Utilidades.socketManager,getApplicationContext());
            logout.execute();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Log.d("Correcto","Voy a LogoutAsyn ");
        }catch(Exception e){
            Log.d("Error","Errores en logout: "+e);
        }
    }

    public void empleados(View view){
        //tambla empleados="0", columna="dni", dni="12345678A", "123556895B" crud="0" que es select
        SelectEmpleadosAsyn empleadosAsyn = new SelectEmpleadosAsyn(Utilidades.socketManager,getApplicationContext(),"0","0","dni","12345678A","0");
        empleadosAsyn.execute();
    }
}