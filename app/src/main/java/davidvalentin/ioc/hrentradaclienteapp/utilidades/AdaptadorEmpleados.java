package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import davidvalentin.ioc.hrentradaclienteapp.R;
import modelo.Empleados;

public class AdaptadorEmpleados extends RecyclerView.Adapter<AdaptadorEmpleados.ViewHolderEmpleados> {

    ArrayList<Empleados> empleados;


    public AdaptadorEmpleados(ArrayList<Empleados> empleados) {
        this.empleados = empleados;
    }

    @NonNull
    @Override
    public ViewHolderEmpleados onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empleado,null,false);
        return new ViewHolderEmpleados(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderEmpleados holder, int position) {
        holder.dni.setText(empleados.get(position).getDni());
        holder.nom.setText(empleados.get(position).getNom());
        holder.apellido.setText(empleados.get(position).getApellido());
        holder.nomempresa.setText(empleados.get(position).getNomEmpresa());
        holder.departament.setText(empleados.get(position).getDepartament());
        holder.codicard.setText(String.valueOf(empleados.get(position).getCodiCard()));
        holder.mail.setText(empleados.get(position).getMail());
        holder.telephon.setText(String.valueOf(empleados.get(position).getTelefono()));
    }

    @Override
    public int getItemCount() {
        return empleados.size();
    }

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
