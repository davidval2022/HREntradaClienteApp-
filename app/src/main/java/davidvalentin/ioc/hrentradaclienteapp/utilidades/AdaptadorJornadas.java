package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import davidvalentin.ioc.hrentradaclienteapp.R;
import modelo.Jornada;

public class AdaptadorJornadas extends RecyclerView.Adapter<AdaptadorJornadas.ViewHolderJornadas> implements View.OnClickListener {

    ArrayList<Jornada> jornadas;
    private View.OnClickListener listener;


    public AdaptadorJornadas(ArrayList<Jornada> jornadas) {
        this.jornadas = jornadas;
    }

    @NonNull
    @Override
    public ViewHolderJornadas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jornada,null,false);
        view.setOnClickListener(this);
        return new ViewHolderJornadas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderJornadas holder, int position) {
        holder.dni.setText(jornadas.get(position).getDni());
        holder.nom.setText(jornadas.get(position).getNom());
        holder.apellido.setText(jornadas.get(position).getApellido());
        holder.codicard.setText(String.valueOf(jornadas.get(position).getCodicard()));
        holder.horaentrada.setText(jornadas.get(position).getHoraentrada());
        holder.horasalida.setText(jornadas.get(position).getHorasalida());
        holder.total.setText(jornadas.get(position).getTotal());
        holder.fecha.setText(jornadas.get(position).getFecha());



    }

    @Override
    public int getItemCount() {
        return jornadas.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }

    }

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
