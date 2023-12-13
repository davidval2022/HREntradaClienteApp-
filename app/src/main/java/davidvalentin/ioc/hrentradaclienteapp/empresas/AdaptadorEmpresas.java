package davidvalentin.ioc.hrentradaclienteapp.empresas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import davidvalentin.ioc.hrentradaclienteapp.R;
import modelo.Empresa;

/**
 * @author David Valentin Mateo
 * Clase AdaptadorEmpresas: Adapta y gestiona la presentación de datos de empresas en un RecyclerView.
 * Esta clase extiende  de RecyclerView.Adapter y se encarga de manejar la creación de vistas y la vinculación
 * de datos para la presentación de empresas en el RecyclerView.
 *
 */
public class AdaptadorEmpresas extends RecyclerView.Adapter<AdaptadorEmpresas.ViewHolderEmpresas> implements View.OnClickListener {
    // Lista de empresas que se mostrarán en el RecyclerView
    ArrayList<Empresa> empresas;
    // Interfaz de escucha para eventos de clic en los diferentes elementos del RecyclerView
    private View.OnClickListener listener;


    public AdaptadorEmpresas(ArrayList<Empresa> empresas) {
        this.empresas = empresas;
    }


    /**
     * Método  llamado cuando se crea un nuevo ViewHolder.
     * @param parent grupo al que se adjuntará la nueva vista.
     * @param viewType Tipo de vista que se está creando.
     * @return Nuevo ViewHolderEmpresas creado para representar un elemento de empresa.
     */
    @NonNull
    @Override
    public ViewHolderEmpresas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empresa,null,false);
        view.setOnClickListener(this);
        return new ViewHolderEmpresas(view);
    }

    /**
     * Método llamado para actualizar la información mostrada en el ViewHolder.
     *
     * @param holder   ViewHolder que debe actualizarse con los datos de la empresa.
     * @param position Posición del empleado en la lista de empresas.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderEmpresas holder, int position) {
        holder.nom.setText(empresas.get(position).getNom());
        holder.address.setText(empresas.get(position).getAddress());
        holder.telephon.setText(String.valueOf(empresas.get(position).getTelephon()));

    }

    /**
     * Obtiene el número total de elementos en la lista de empresas.
     *
     * @return Número total de empresas.
     */
    @Override
    public int getItemCount() {
        return empresas.size();
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
     * Clase interna ViewHolderEmpresas: Representa la vista de un elemento de empresas en el RecyclerView.
     */
    public class ViewHolderEmpresas extends RecyclerView.ViewHolder {
        TextView nom,address,telephon;


        public ViewHolderEmpresas(@NonNull View itemView) {
            super(itemView);
            nom = (TextView) itemView.findViewById(R.id.nom);
            address = (TextView) itemView.findViewById(R.id.address);
            telephon = (TextView) itemView.findViewById(R.id.telephon);

        }
    }
}
