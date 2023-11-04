package davidvalentin.ioc.hrentradaclienteapp.utilidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import davidvalentin.ioc.hrentradaclienteapp.R;
import modelo.Empresa;

public class AdaptadorEmpresas extends RecyclerView.Adapter<AdaptadorEmpresas.ViewHolderEmpresas> implements View.OnClickListener {

    ArrayList<Empresa> empresas;
    private View.OnClickListener listener;


    public AdaptadorEmpresas(ArrayList<Empresa> empresas) {
        this.empresas = empresas;
    }

    @NonNull
    @Override
    public ViewHolderEmpresas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empresa,null,false);
        view.setOnClickListener(this);
        return new ViewHolderEmpresas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderEmpresas holder, int position) {
        holder.nom.setText(empresas.get(position).getNom());
        holder.address.setText(empresas.get(position).getAddress());
        holder.telephon.setText(String.valueOf(empresas.get(position).getTelephon()));

    }

    @Override
    public int getItemCount() {
        return empresas.size();
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
