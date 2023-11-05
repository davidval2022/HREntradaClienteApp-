package davidvalentin.ioc.hrentradaclienteapp;

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

import davidvalentin.ioc.hrentradaclienteapp.utilidades.AdaptadorEmpresas;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.AdaptadorUsers;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SelectEmpresasAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SelectUsersAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

public class EmpresasActivity extends AppCompatActivity {

    //necestio que esta activity  implemente la interfaz SelectEmpresasAsyn
    //para que actualice los datos en tiempo real en la UI
    Spinner comboCamposEmpresas;
    RecyclerView recyclerEmpresas;
    AdaptadorEmpresas mAdapter;
    RecyclerView.LayoutManager layoutManager;
    String nombreCampoFiltro;
    EditText editTextFiltro;
    String palabraFiltro = "-1"; // por defecto la palabra a buscar es tambien 0
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresas);

        nombreCampoFiltro = "0"; //por defecto es 0

        editTextFiltro = findViewById(R.id.textFiltroEmpresas);//obtenermos la referencia del campo de texto para los filtros

        comboCamposEmpresas = (Spinner) findViewById(R.id.spinCamposEmpresas);
        ArrayAdapter<CharSequence> adapterCampos = ArrayAdapter.createFromResource(this,R.array.combo_empresas,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        comboCamposEmpresas.setAdapter(adapterCampos);
        comboCamposEmpresas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        recyclerEmpresas = findViewById(R.id.RecyclerEmpresas);
        layoutManager = new LinearLayoutManager(this);
        recyclerEmpresas.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorEmpresas(Utilidades.listaEmpresas);
        recyclerEmpresas.setAdapter(mAdapter);


        //Según lo establecido,  el primer 0 es  consulta de tipo select y el segundo 0 es la tabla empleados
        SelectEmpresasAsyn empresasAsyn = new SelectEmpresasAsyn(Utilidades.socketManager,getApplicationContext(),"0","2","0","0","0",recyclerEmpresas,mAdapter,layoutManager);
        empresasAsyn.execute();

        //String m = pruebaMensajeToast();
        // Log.d("Correcto_Em: ",m);
        mAdapter.notifyDataSetChanged();
    }

    public  void mostrarToast(String mensaje){
        Toast.makeText(this, "Mensaje: "+mensaje, Toast.LENGTH_LONG).show();


    }

    public void filtrarEmpresas(View view) {
        Utilidades.listaEmpresas.clear();
        recyclerEmpresas = findViewById(R.id.RecyclerEmpresas);
        layoutManager = new LinearLayoutManager(this);
        recyclerEmpresas.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorEmpresas(Utilidades.listaEmpresas);
        recyclerEmpresas.setAdapter(mAdapter);
        palabraFiltro = editTextFiltro.getText().toString();

        SelectEmpresasAsyn empresasAsyn;
        if(palabraFiltro.equalsIgnoreCase("-1") || palabraFiltro.equalsIgnoreCase("")){
            empresasAsyn = new SelectEmpresasAsyn(Utilidades.socketManager,getApplicationContext(),"0","2","0","0","0",recyclerEmpresas,mAdapter,layoutManager);
        }else{
            empresasAsyn = new SelectEmpresasAsyn(Utilidades.socketManager,getApplicationContext(),"0","2",nombreCampoFiltro,palabraFiltro,"0",recyclerEmpresas,mAdapter,layoutManager);
        }
        //Según lo establecido,  el primer 0 es  consulta de tipo select y el segundo 0 es la tabla empleados
        empresasAsyn.execute();
        mAdapter.notifyDataSetChanged();
        if(!Utilidades.mensajeDelServer.equals("")){
            Utilidades.mensajeDelServer = "";
        }
    }

    public void volver(View view){
        if(Utilidades.tipoUser == 1){
            Intent intent = new Intent(this, MenuUserActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, MenuAdminActivity.class);
            startActivity(intent);
        }

    }
}