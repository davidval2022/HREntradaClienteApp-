package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.LogoutAsyn;
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
}