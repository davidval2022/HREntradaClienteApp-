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

/**
 * Activity que asociada al menú de usuario de tipo normal (1)
 */
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
    /**
     * Metodo asociado al botón logout, lo que es desconectarnos del server enviandole un
     * mensaje para ello. Luego nos reenvia a la pantalla de login.
     * También utilizamos un clase de tipo Asyntask para hacer el logout
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
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

    /**
     * Método asociado al botón 'empleados', mediante este método y botón somos redirigidos
     * a la pantalla de empleados
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void empleados(View view){
        Intent intent = new Intent(this, EmpleadosActivity.class);
        startActivity(intent);
    }

    /**
     * Método asociado al botón 'empresas', mediante este método y botón somos redirigidos
     * a la pantalla de empresas
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void empresas(View view) {
        Intent intent = new Intent(this,  EmpresasActivity.class);
        startActivity(intent);
    }

    /**
     * Método asociado al botón 'jornadas', mediante este método y botón somos redirigidos
     * a la pantalla de jornadas
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void jornadas(View view){
        Intent intent = new Intent(this,  JornadasActivity.class);
        startActivity(intent);
    }

}