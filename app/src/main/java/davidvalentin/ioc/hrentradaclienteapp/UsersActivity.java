package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.AdaptadorEmpleados;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.AdaptadorUsers;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SelectEmpleadosAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SelectUsersAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

public class UsersActivity extends AppCompatActivity {

    //necestio que esta activity  implemente la interfaz SelectEmpleadosAsyn.AsyncResponse
    //para que actualice los datos en tiempo real en la UI
    Spinner comboCamposUsers;
    RecyclerView recyclerViewUsers;
    //RecyclerView.Adapter mAdapter;
    AdaptadorUsers mAdapter;
    RecyclerView.LayoutManager layoutManager;
    String nombreCampoFiltro;
    EditText editTextFiltro;
    String palabraFiltro = "0"; // por defecto la palabra a buscar es tambien 0
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        nombreCampoFiltro = "0"; //por defecto es 0

        editTextFiltro = findViewById(R.id.textFiltroUsers);//obtenermos la referencia del campo de texto para los filtros

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


        //Según lo establecido,  el primer 0 es  consulta de tipo select y el segundo 0 es la tabla empleados
        SelectUsersAsyn usersAsyn = new SelectUsersAsyn(Utilidades.socketManager,getApplicationContext(),"0","1","0","0","0",recyclerViewUsers,mAdapter,layoutManager);
        usersAsyn.execute();

        //String m = pruebaMensajeToast();
        // Log.d("Correcto_Em: ",m);
        mAdapter.notifyDataSetChanged();

    }

    public  void mostrarToast(String mensaje){
        Toast.makeText(this, "Mensaje: "+mensaje, Toast.LENGTH_LONG).show();


    }


    public void filtrarUsers(View view) {
        Utilidades.listaUsers.clear();
        recyclerViewUsers = findViewById(R.id.RecyclerUsers);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewUsers.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorUsers(Utilidades.listaUsers);
        recyclerViewUsers.setAdapter(mAdapter);
        palabraFiltro = editTextFiltro.getText().toString();
        //mostrarToast("Campo filtro: "+nombreCampoFiltro+" palabra: "+palabraFiltro);
        if(palabraFiltro.equalsIgnoreCase("")){
            palabraFiltro = "0";
        }
        //Según lo establecido,  el primer 0 es  consulta de tipo select y el segundo 0 es la tabla empleados
        SelectUsersAsyn usersAsyn = new SelectUsersAsyn(Utilidades.socketManager,getApplicationContext(),"0","1",nombreCampoFiltro,palabraFiltro,"0",recyclerViewUsers,mAdapter,layoutManager);
        usersAsyn.execute();
        mAdapter.notifyDataSetChanged();
        if(!Utilidades.mensajeDelServer.equals("")){
            mostrarToast(Utilidades.mensajeDelServer);

        }
        Utilidades.mensajeDelServer = "";
    }

    public void volver(View view){
        Intent intent = new Intent(this, MenuAdminActivity.class);
        startActivity(intent);
    }
}