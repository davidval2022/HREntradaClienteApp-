package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import davidvalentin.ioc.hrentradaclienteapp.utilidades.AdaptadorEmpleados;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.SelectEmpleadosAsyn;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

public class EmpleadosActivity extends AppCompatActivity{
    //necestio que esta activity  implemente la interfaz SelectEmpleadosAsyn.AsyncResponse
    //para que actualice los datos en tiempo real en la UI
    Spinner comboCamposEmpleados;
    private RecyclerView recyclerViewEmpleados;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String nombreCampoFiltro;
    private EditText editTextFiltro;
    private String palabraFiltro = "0"; // por defecto la palabra a buscar es tambien 0
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados);

        nombreCampoFiltro = "0"; //por defecto es 0

        editTextFiltro = findViewById(R.id.textFiltroEmpl);//obtenermos la referencia del campo de texto para los filtros

        comboCamposEmpleados = (Spinner) findViewById(R.id.spinCamposEmpleados);
        ArrayAdapter<CharSequence> adapterCampos = ArrayAdapter.createFromResource(this,R.array.combo_empleados,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        comboCamposEmpleados.setAdapter(adapterCampos);
        comboCamposEmpleados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mostrarToast((String) adapterCampos.getItem(position));
                //obtenemos el campo a filtrar  seleccionado del spinner
                nombreCampoFiltro = (String) adapterCampos.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //obtenemos la palabra a buscar



        recyclerViewEmpleados = findViewById(R.id.RecyclerEmpleados);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewEmpleados.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorEmpleados(Utilidades.listaEmpleados);
        recyclerViewEmpleados.setAdapter(mAdapter);

        //Según lo establecido,  el primer 0 es  consulta de tipo select y el segundo 0 es la tabla empleados
        SelectEmpleadosAsyn empleadosAsyn = new SelectEmpleadosAsyn(Utilidades.socketManager,getApplicationContext(),"0","0","0","0","0",recyclerViewEmpleados,mAdapter,layoutManager);
        empleadosAsyn.execute();
        //recycler
        //recyclerViewEmpleados.setHasFixedSize(true);
        // Usar un administrador para el RecyclerView
       // layoutManager = new LinearLayoutManager(this);
       // recyclerViewEmpleados.setLayoutManager(layoutManager);
        // Crear un adaptador y establecerlo en el RecyclerView
        //mAdapter = new AdaptadorEmpleados(Utilidades.listaEmpleados); // Asegúrate de reemplazar MyAdapter con tu propio adaptador
        //recyclerViewEmpleados.setAdapter(mAdapter);

        String m = pruebaMensajeToast();
        mostrarToast(m);
        Log.d("Correcto_Em: ",m);
        mAdapter.notifyDataSetChanged();

    }

    public  void mostrarToast(String mensaje){
        Toast.makeText(this, "Mensaje: "+mensaje, Toast.LENGTH_LONG).show();


    }

    public String pruebaMensajeToast(){
        String mensaje = "Primera linea:\n";
        for(int i = 0; i< Utilidades.listaEmpleados.size(); i++){
            //mensaje+="Nombre: " + listaEmpleados.get(i).getNom() + " Apellidos: " + listaEmpleados.get(i).getApellido()+ " DNI: "+listaEmpleados.get(i).getDni()+"\n";
            mensaje+= "Correcto_Em: Nombre: " + Utilidades.listaEmpleados.get(i).getNom() + " Apellidos: " + Utilidades.listaEmpleados.get(i).getApellido()+ " DNI: "+Utilidades.listaEmpleados.get(i).getDni()+
                    " Apellido: "+Utilidades.listaEmpleados.get(i).getApellido()+" Codicard: "+Utilidades.listaEmpleados.get(i).getCodiCard()+
                    " Telefono: "+Utilidades.listaEmpleados.get(i).getTelefono()+"\n";
        }
        return mensaje;

    }


    public void filtrarEmpleados(View view) {
        Utilidades.listaEmpleados.clear();
        recyclerViewEmpleados = findViewById(R.id.RecyclerEmpleados);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewEmpleados.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorEmpleados(Utilidades.listaEmpleados);
        recyclerViewEmpleados.setAdapter(mAdapter);
        palabraFiltro = editTextFiltro.getText().toString();
        mostrarToast("Campo filtro: "+nombreCampoFiltro+" palabra: "+palabraFiltro);
        if(palabraFiltro.equalsIgnoreCase("")){
            palabraFiltro = "0";
        }
        //Según lo establecido,  el primer 0 es  consulta de tipo select y el segundo 0 es la tabla empleados
        SelectEmpleadosAsyn empleadosAsyn = new SelectEmpleadosAsyn(Utilidades.socketManager,getApplicationContext(),"0","0",nombreCampoFiltro,palabraFiltro,"0",recyclerViewEmpleados,mAdapter,layoutManager);
        empleadosAsyn.execute();
        mAdapter.notifyDataSetChanged();
    }
}