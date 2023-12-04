package davidvalentin.ioc.hrentradaclienteapp.empleados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import davidvalentin.ioc.hrentradaclienteapp.R;
import modelo.Empleados;

/**
 * Clase AdaptadorEmpleados: Adapta y gestiona la presentación de datos de empleados en un RecyclerView.
 * Esta clase extiende  de RecyclerView.Adapter y se encarga de manejar la creación de vistas y la vinculación
 * de datos para la presentación de empleados en el RecyclerView.
 */
public class AdaptadorEmpleados extends RecyclerView.Adapter<AdaptadorEmpleados.ViewHolderEmpleados> implements View.OnClickListener {
    // Lista de empleados que se mostrarán en el RecyclerView
    private ArrayList<Empleados> empleados;
    // Interfaz de escucha para eventos de clic en los diferentes elementos del RecyclerView
    private View.OnClickListener listener;


    public AdaptadorEmpleados(ArrayList<Empleados> empleados) {
        this.empleados = empleados;
    }

    /**
     * Método  llamado cuando se crea un nuevo ViewHolder.
     * @param parent grupo al que se adjuntará la nueva vista.
     * @param viewType Tipo de vista que se está creando.
     * @return Nuevo ViewHolderEmpleados creado para representar un elemento de empleado.
     */
    @NonNull
    @Override
    public ViewHolderEmpleados onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empleado,null,false);
        view.setOnClickListener(this);
        return new ViewHolderEmpleados(view);
    }

    /**
     * Método llamado para actualizar la información mostrada en el ViewHolder.
     *
     * @param holder   ViewHolder que debe actualizarse con los datos del empleado.
     * @param position Posición del empleado en la lista de empleados.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderEmpleados holder, int position) {
        holder.dni.setText(empleados.get(position).getDni());
        holder.nom.setText(empleados.get(position).getNom());
        holder.apellido.setText(empleados.get(position).getApellido());
        holder.nomempresa.setText(empleados.get(position).getNomempresa());
        holder.departament.setText(empleados.get(position).getDepartament());
        holder.codicard.setText(empleados.get(position).getCodicard());
        holder.mail.setText(empleados.get(position).getMail());
        holder.telephon.setText(String.valueOf(empleados.get(position).getTelephon()));
    }

    /**
     * Obtiene el número total de elementos en la lista de empleados.
     *
     * @return Número total de empleados.
     */
    @Override
    public int getItemCount() {
        return empleados.size();
    }
    /**
     * Establece el objeto de escucha para eventos de clic en elementos del RecyclerView.
     *
     * @param listener Objeto de escucha para eventos de clic.
     */
    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;

    }

    /**
     * Método llamado cuando se hace clic en un elemento del RecyclerView.
     *
     * @param view Vista en la que se hizo clic.
     */
    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }

    }

    /**
     * Clase interna ViewHolderEmpleados: Representa la vista de un elemento de empleado en el RecyclerView.
     */
    public class ViewHolderEmpleados extends RecyclerView.ViewHolder {
        TextView dni,nom,apellido,nomempresa,departament,codicard,mail,telephon;

        public ViewHolderEmpleados(@NonNull View itemView) {
            super(itemView);
            dni = (TextView) itemView.findViewById(R.id.dni);
            nom = (TextView) itemView.findViewById(R.id.nom);
            apellido = (TextView) itemView.findViewById(R.id.apellido);
            nomempresa = (TextView) itemView.findViewById(R.id.empresa);
            departament = (TextView) itemView.findViewById(R.id.departament);
            codicard = (TextView) itemView.findViewById(R.id.codicard);
            mail = (TextView) itemView.findViewById(R.id.mail);
            telephon = (TextView) itemView.findViewById(R.id.telephon);
        }
    }
}
