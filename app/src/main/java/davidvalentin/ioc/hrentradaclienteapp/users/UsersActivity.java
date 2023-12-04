package davidvalentin.ioc.hrentradaclienteapp.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import davidvalentin.ioc.hrentradaclienteapp.MenuAdminActivity;
import davidvalentin.ioc.hrentradaclienteapp.R;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

/**
 *  Activity asociada a la pantalla principal de users. Desde aquí podemos ver  todos los users,
 *  podemos filtrar por diferentes campos y tenemos un botón para ir a otra pantalla donde
 *  poder crear nuevos users
 */

public class UsersActivity extends AppCompatActivity {

    Spinner comboCamposUsers;
    RecyclerView recyclerViewUsers;
    AdaptadorUsers mAdapter;
    RecyclerView.LayoutManager layoutManager;
    String nombreCampoFiltro;
    EditText editTextFiltro;
    String palabraFiltro = "-1"; // por defecto la palabra a buscar es tambien 0
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        nombreCampoFiltro = "0"; //por defecto es 0

        editTextFiltro = findViewById(R.id.textFiltroUsers);//obtenermos la referencia del campo de texto para los filtros
        //El spinner mostrará los datos estaticos guardados en campos_users
        comboCamposUsers = (Spinner) findViewById(R.id.spinCamposUsers);
        ArrayAdapter<CharSequence> adapterCampos = ArrayAdapter.createFromResource(this,R.array.combo_users,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        comboCamposUsers.setAdapter(adapterCampos);
        comboCamposUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // mostrarToast((String) adapterCampos.getItem(position));
                //obtenemos el campo a filtrar  seleccionado del spinner
                nombreCampoFiltro = (String) adapterCampos.getItem(position);
                if(nombreCampoFiltro.equalsIgnoreCase("Selecciona un campo a filtrar")){
                    nombreCampoFiltro = "0";
                };
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        recyclerViewUsers = findViewById(R.id.RecyclerUsers);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewUsers.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorUsers(Utilidades.listaUsers);
        recyclerViewUsers.setAdapter(mAdapter);


        //Según lo establecido,  el primer 0 es  consulta de tipo select y el 1 es la tabla users
        SelectUsersAsyn usersAsyn = new SelectUsersAsyn(Utilidades.socketManager,getApplicationContext(),"0","1","0","0","0",recyclerViewUsers,mAdapter,layoutManager);
        usersAsyn.execute();

        //String m = pruebaMensajeToast();
        // Log.d("Correcto_Em: ",m);
        mAdapter.notifyDataSetChanged();

    }

    /**
     * Metodo que lanza un mensaje de Toast
     * @param mensaje es el mensaje que mostrará el Toast
     */
    public  void mostrarToast(String mensaje){
        Toast.makeText(this, "Mensaje: "+mensaje, Toast.LENGTH_LONG).show();


    }

    /**
     * Método asociado al botón 'filtrar', con este método filtramos un usario o varios, dependiendo
     * de la palabra que introduzcamos y del campo que seleccionemos del spinner.
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void filtrarUsers(View view) {
        Utilidades.listaUsers.clear();
        recyclerViewUsers = findViewById(R.id.RecyclerUsers);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewUsers.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorUsers(Utilidades.listaUsers);
        recyclerViewUsers.setAdapter(mAdapter);
        palabraFiltro = editTextFiltro.getText().toString();

        SelectUsersAsyn usersAsyn;
        if(palabraFiltro.equalsIgnoreCase("-1") || palabraFiltro.equalsIgnoreCase("")){
            usersAsyn = new SelectUsersAsyn(Utilidades.socketManager,getApplicationContext(),"0","1","0","0","0",recyclerViewUsers,mAdapter,layoutManager);
        }else{
            usersAsyn = new SelectUsersAsyn(Utilidades.socketManager,getApplicationContext(),"0","1",nombreCampoFiltro,palabraFiltro,"0",recyclerViewUsers,mAdapter,layoutManager);
        }
        usersAsyn.execute();
        mAdapter.notifyDataSetChanged();
        if(!Utilidades.mensajeDelServer.equals("")){
            Utilidades.mensajeDelServer = "";
        }
    }

    /**
     * Metodo asociado al botón 'volver'. Con este método somo redirigidos a MenuAdminActivity
     */
    public void volverAUsers(View view){
        Intent intent = new Intent(this, MenuAdminActivity.class);
        startActivity(intent);
    }

    /**
     * Método asociado al botón 'nuevo'. Somo redirigidos a la activity UsersInsertActivity para
     * desde allí crear un nuevo usuario
     */
    public void nuevoUser(View view){
        Intent intent = new Intent(this, UsersInsertActivity.class);
        startActivity(intent);
    }


}