package davidvalentin.ioc.hrentradaclienteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EmpleadosActivity extends AppCompatActivity {

    Spinner comboCamposEmpleados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados);

        comboCamposEmpleados = (Spinner) findViewById(R.id.spinCamposEmpleados);
        ArrayAdapter<CharSequence> adapterCampos = ArrayAdapter.createFromResource(this,R.array.combo_empleados,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        comboCamposEmpleados.setAdapter(adapterCampos);
    }
}