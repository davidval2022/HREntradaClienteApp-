package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import davidvalentin.ioc.hrentradaclienteapp.R;
import modelo.Users;

/**
 * Clase AdaptadorUsers: Adapta y gestiona la presentación de datos de users en un RecyclerView.
 * Esta clase extiende  de RecyclerView.Adapter y se encarga de manejar la creación de vistas y la vinculación
 * de datos para la presentación de users en el RecyclerView.
 */
public class AdaptadorUsers extends RecyclerView.Adapter<AdaptadorUsers.ViewHolderUsers> implements View.OnClickListener {

    ArrayList<Users> users;
    private View.OnClickListener listener;


    public AdaptadorUsers(ArrayList<Users> users) {
        this.users = users;
    }


    /**
     * Método  llamado cuando se crea un nuevo ViewHolder.
     * @param parent grupo al que se adjuntará la nueva vista.
     * @param viewType Tipo de vista que se está creando.
     * @return Nuevo ViewHolderUsers creado para representar un elemento de user.
     */
    @NonNull
    @Override
    public ViewHolderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,null,false);
        view.setOnClickListener(this);
        return new ViewHolderUsers(view);
    }


    /**
     * Método llamado para actualizar la información mostrada en el ViewHolder.
     *
     * @param holder   ViewHolder que debe actualizarse con los datos del user.
     * @param position Posición del user en la lista de users.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderUsers holder, int position) {
        holder.login.setText(users.get(position).getLogin());
        holder.numtipe.setText(String.valueOf(users.get(position).getNumtipe()));
        holder.dni.setText(users.get(position).getDni());

    }

    /**
     * Obtiene el número total de elementos en la lista de users.
     *
     * @return Número total de users.
     */
    @Override
    public int getItemCount() {
        return users.size();
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
     * Clase interna ViewHolderUsers: Representa la vista de un elemento de user en el RecyclerView.
     */
    public class ViewHolderUsers extends RecyclerView.ViewHolder {
        TextView login,numtipe,dni;

        public ViewHolderUsers(@NonNull View itemView) {
            super(itemView);
            dni = (TextView) itemView.findViewById(R.id.dni);
            login = (TextView) itemView.findViewById(R.id.login);
            numtipe = (TextView) itemView.findViewById(R.id.numtipe);

        }
    }
}
