package davidvalentin.ioc.hrentradaclienteapp.empleados;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import davidvalentin.ioc.hrentradaclienteapp.MenuAdminActivity;
import davidvalentin.ioc.hrentradaclienteapp.MenuUserActivity;
import davidvalentin.ioc.hrentradaclienteapp.R;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

/**
 *  @author David Valentin Mateo
 *  Activity asociada a la pantalla principal de empleados. Desde aquí podemos ver  todos los empleados,
 *  podemos filtrar por diferentes campos y tenemos un botón para ir a otra pantalla donde
 *  poder crear nuevos empleados
 */

public class EmpleadosActivity extends AppCompatActivity{

    Spinner comboCamposEmpleados;
    RecyclerView recyclerViewEmpleados;
    AdaptadorEmpleados mAdapter;
    RecyclerView.LayoutManager layoutManager;
    String nombreCampoFiltro;
    EditText editTextFiltro;
    String palabraFiltro = "-1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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


        recyclerViewEmpleados = findViewById(R.id.RecyclerEmpleados);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewEmpleados.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorEmpleados(Utilidades.listaEmpleados);
        recyclerViewEmpleados.setAdapter(mAdapter);


        //Según lo establecido,  el primer 0 es  consulta de tipo select y el segundo 0 es la tabla empleados
        SelectEmpleadosAsyn empleadosAsyn = new SelectEmpleadosAsyn(Utilidades.socketManager,getApplicationContext(),"0","0","0","0","0",recyclerViewEmpleados,mAdapter,layoutManager);
        empleadosAsyn.execute();

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
     * Método asociado al botón 'filtrar', con este método filtramos un empleado o varios, dependiendo
     * de la palabra que introduzcamos y del campo que seleccionemos del spinner.
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void filtrarEmpleados(View view) {
        Utilidades.listaEmpleados.clear();
        recyclerViewEmpleados = findViewById(R.id.RecyclerEmpleados);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewEmpleados.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorEmpleados(Utilidades.listaEmpleados);
        recyclerViewEmpleados.setAdapter(mAdapter);
        palabraFiltro = editTextFiltro.getText().toString();
        SelectEmpleadosAsyn empleadosAsyn;
        //si palabraFiltro está vacion devolvemos todos los registros
        if(palabraFiltro.equalsIgnoreCase("-1") || palabraFiltro.equals("")){
            empleadosAsyn = new SelectEmpleadosAsyn(Utilidades.socketManager,getApplicationContext(),"0","0","0","0","0",recyclerViewEmpleados,mAdapter,layoutManager);
        }else{
             empleadosAsyn = new SelectEmpleadosAsyn(Utilidades.socketManager,getApplicationContext(),"0","0",nombreCampoFiltro,palabraFiltro,"0",recyclerViewEmpleados,mAdapter,layoutManager);
        }
        empleadosAsyn.execute();
        mAdapter.notifyDataSetChanged();
        if(!Utilidades.mensajeDelServer.equals("")){
            Utilidades.mensajeDelServer = "";
        }

    }

    /**
     * Metodo asociado al botón 'volver'. Con este método somo redirigidos a
     * bien al MenuAdminActivity,o bien al menú MenuUserActivity dependiendo de si el usuario
     * es de tipo 1 o 0
     */
    public void volver(View view) {
        if(Utilidades.tipoUser == 0){
            Intent intent = new Intent(this, MenuAdminActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, MenuUserActivity.class);
            startActivity(intent);
        }
    }
    /**
     * Método asociado al botón 'nuevo'. Somo redirigidos a la activity EmpleadosInsertActivity para
     * desde allí crear un nuevo empleado
     */
    public void insertarEmpleado(View view){
        Intent intent = new Intent(this, EmpleadosInsertActivity.class);
        startActivity(intent);
    }
}