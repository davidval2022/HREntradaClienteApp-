package davidvalentin.ioc.hrentradaclienteapp.jornadas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import davidvalentin.ioc.hrentradaclienteapp.R;
import modelo.Jornada;

/**
 * Clase AdaptadorJornadas: Adapta y gestiona la presentación de datos de jornadas en un RecyclerView.
 * Esta clase extiende  de RecyclerView.Adapter y se encarga de manejar la creación de vistas y la vinculación
 * de datos para la presentación de jornadas en el RecyclerView.
 */
public class AdaptadorJornadas extends RecyclerView.Adapter<AdaptadorJornadas.ViewHolderJornadas> implements View.OnClickListener {

    ArrayList<Jornada> jornadas;
    private View.OnClickListener listener;


    public AdaptadorJornadas(ArrayList<Jornada> jornadas) {
        this.jornadas = jornadas;
    }


    /**
     * Método  llamado cuando se crea un nuevo ViewHolder.
     * @param parent grupo al que se adjuntará la nueva vista.
     * @param viewType Tipo de vista que se está creando.
     * @return new ViewHolderJornadas creado para representar un elemento de jornada.
     */

    @NonNull
    @Override
    public ViewHolderJornadas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jornada,null,false);
        view.setOnClickListener(this);
        return new ViewHolderJornadas(view);
    }

    /**
     * Método llamado para actualizar la información mostrada en el ViewHolder.
     *
     * @param holder   ViewHolder que debe actualizarse con los datos de la jornada.
     * @param position Posición de la jornada en la lista de jonradas.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderJornadas holder, int position) {
        holder.dni.setText(jornadas.get(position).getDni());
        holder.nom.setText(jornadas.get(position).getNom());
        holder.apellido.setText(jornadas.get(position).getApellido());
        holder.codicard.setText(jornadas.get(position).getCodicard());
        holder.horaentrada.setText(jornadas.get(position).getHoraentrada());
        holder.horasalida.setText(jornadas.get(position).getHorasalida());
        holder.total.setText(jornadas.get(position).getTotal());
        holder.fecha.setText(jornadas.get(position).getFecha());



    }

    /**
     * Obtiene el número total de elementos en la lista de jornadas.
     *
     * @return Número total de jornadas.
     */
    @Override
    public int getItemCount() {
        return jornadas.size();
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
     * Clase interna ViewHolderJornadas: Representa la vista de un elemento de jornada en el RecyclerView.
     */
    public class ViewHolderJornadas extends RecyclerView.ViewHolder {
        TextView dni,nom,apellido,codicard,horaentrada,horasalida,total,fecha;


        public ViewHolderJornadas(@NonNull View itemView) {
            super(itemView);
            dni = (TextView) itemView.findViewById(R.id.dni);
            nom = (TextView) itemView.findViewById(R.id.nom);
            apellido = (TextView) itemView.findViewById(R.id.apellido);
            codicard = (TextView) itemView.findViewById(R.id.codicard);
            horaentrada = (TextView) itemView.findViewById(R.id.horaentrada);
            horasalida = (TextView) itemView.findViewById(R.id.horasalida);
            fecha = (TextView) itemView.findViewById(R.id.fecha);
            total = (TextView) itemView.findViewById(R.id.total);



        }
    }
}
