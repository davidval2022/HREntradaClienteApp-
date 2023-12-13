package davidvalentin.ioc.hrentradaclienteapp.jornadas;

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
import davidvalentin.ioc.hrentradaclienteapp.MenuUserActivity;
import davidvalentin.ioc.hrentradaclienteapp.R;
import davidvalentin.ioc.hrentradaclienteapp.utilidades.Utilidades;

/**
 * @author David Valentin Mateo
 *  Activity asociada a la pantalla principal de jornadas. Desde aquí podemos ver todas las
 *  jornadas, podemos filtrar por diferentes campos, en este caso
 *  y  a diferencia de las otras activitys similares a esta, tenemos dos campos donde poder
 *  intrudicir los parámetros a buscar dependiendo claro de lo que
 *  hayamos escogido en el spinner. Tenemos la opción de escoger 1 o 2 campos.
 *  También tenemos un botón para ir a otra pantalla donde
 *  poder crear nuevos inicios de jornadas. La jornada completa se completará actualizando la
 *  salida del empleado.
 */
public class JornadasActivity extends AppCompatActivity {

    Spinner comboCamposJornadas;
    RecyclerView recyclerJornadas;
    AdaptadorJornadas mAdapter;
    RecyclerView.LayoutManager layoutManager;
    String nombreCampoFiltro;//solo tenemos un combo box que incluye una palabra compuesta por dos campos
    EditText editTextFiltro1;
    EditText editTextFiltro2;
    String palabraFiltro1 = "-1"; // por defecto la palabra a buscar es  -1
    String palabraFiltro2 = "-1"; // por defecto la palabra a buscar es  -1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //agrego esta linea de abajo para que mantega la pantalla en vertical y tiene que ir justa aquí
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jornadas);

        editTextFiltro1 = findViewById(R.id.textFiltroJor1);//obtenermos la referencia del campo de texto para los filtros
        editTextFiltro2 = findViewById(R.id.textFiltroJor2);//obtenermos la referencia del campo de texto para los filtros

        comboCamposJornadas = (Spinner) findViewById(R.id.spinCamposJornadas);
        ArrayAdapter<CharSequence> adapterCampos = ArrayAdapter.createFromResource(this,R.array.combo_jornadas,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        comboCamposJornadas.setAdapter(adapterCampos);
        comboCamposJornadas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        recyclerJornadas = findViewById(R.id.RecyclerJornadas);
        layoutManager = new LinearLayoutManager(this);
        recyclerJornadas.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorJornadas(Utilidades.listaJornadas);
        recyclerJornadas.setAdapter(mAdapter);


        //Según lo establecido,  el primer 0 es  consulta de tipo select y el  3 es la tabla jornadas
        SelectJornadaAsyn jornadasAsyn = new SelectJornadaAsyn(Utilidades.socketManager,getApplicationContext(),"0","3","0","0","0","0","0",recyclerJornadas,mAdapter,layoutManager);
        jornadasAsyn.execute();
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
     * Método asociado al botón 'filtrar', con este método filtramos una jornada o varias, dependiendo
     * de la(s) palabra(s) que introduzcamos y del campo que seleccionemos del spinner.
     * @param view representa la vista con la que se está interactuando, no utilizado en este caso
     */
    public void filtrarJornadas(View view) {
        Utilidades.listaJornadas.clear();
        recyclerJornadas = findViewById(R.id.RecyclerJornadas);
        layoutManager = new LinearLayoutManager(this);
        recyclerJornadas.setLayoutManager(layoutManager);
        mAdapter = new AdaptadorJornadas(Utilidades.listaJornadas);
        recyclerJornadas.setAdapter(mAdapter);
        palabraFiltro1 = editTextFiltro1.getText().toString();
        palabraFiltro2 = editTextFiltro2.getText().toString();
        SelectJornadaAsyn jornadaAsyn;

        String columna1 = "0";
        String columna2 ="0";
        if(nombreCampoFiltro.equals("dni-fecha")){
            columna1 = "dni";
            columna2 = "fecha";

        }else if(nombreCampoFiltro.equals("nom-fecha")){
            columna1 = "nom";
            columna2 = "fecha";

        }else if(nombreCampoFiltro.equals("apellido-fecha")){
            columna1 = "apellido";
            columna2 = "fecha";

        }else if(nombreCampoFiltro.equals("nom-apellido")){
            columna1 = "nom";
            columna2 = "apellido";

        }else if(nombreCampoFiltro.equals("codicard-fecha")){
            columna1 = "codicard";
            columna2 = "fecha";

        }else if(nombreCampoFiltro.equals("codicard")){
            columna1 = "codicard";
            columna2 = "0";
            palabraFiltro2 = "0";
        }else if(nombreCampoFiltro.equals("dni")){
            columna1 = "dni";
            columna2 = "0";
            palabraFiltro2 = "0";
        }else if(nombreCampoFiltro.equals("nom")){
            columna1 = "nom";
            columna2 = "0";
            palabraFiltro2 = "0";
        }else if(nombreCampoFiltro.equals("apellido")){
            columna1 = "apellido";
            columna2 = "0";
            palabraFiltro2 = "0";
        }else if(nombreCampoFiltro.equals("fecha")){
            columna1 = "fecha";
            columna2 = "0";
            palabraFiltro2 = "0";
        }
        //si palabraFiltro está vacion devolvemos todos los registros
        if(palabraFiltro1.equalsIgnoreCase("-1") || palabraFiltro1.equals("") || palabraFiltro2.equalsIgnoreCase("-1") || palabraFiltro2.equals("")  ){
            jornadaAsyn = new SelectJornadaAsyn(Utilidades.socketManager,getApplicationContext(),"0","3","0","0","0","0","0",recyclerJornadas,mAdapter,layoutManager);
        }else{
            jornadaAsyn = new SelectJornadaAsyn(Utilidades.socketManager,getApplicationContext(),"0","3",columna1,palabraFiltro1,columna2,palabraFiltro2,"0",recyclerJornadas,mAdapter,layoutManager);
        }
        jornadaAsyn.execute();
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
     * Método asociado al botón 'nuevo'. Somo redirigidos a la activity JornadasInsertActivity para
     * desde allí crear un nuevo inicio de jornada
     */
    public void nuevaJornada(View view) {
        Intent intent = new Intent(this, JornadasInsertActivity.class);
        startActivity(intent);
    }
}