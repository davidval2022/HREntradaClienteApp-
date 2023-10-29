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

public class AdaptadorUsers extends RecyclerView.Adapter<AdaptadorUsers.ViewHolderUsers> implements View.OnClickListener {

    ArrayList<Users> users;
    private View.OnClickListener listener;


    public AdaptadorUsers(ArrayList<Users> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolderUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,null,false);
        view.setOnClickListener(this);
        return new ViewHolderUsers(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderUsers holder, int position) {
        holder.login.setText(users.get(position).getLogin());
        holder.pass.setText(users.get(position).getPass());
        holder.numtipe.setText(String.valueOf(users.get(position).getNumtipe()));
        holder.dni.setText(users.get(position).getDni());

    }

    @Override
    public int getItemCount() {
        return users.size();
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

    public class ViewHolderUsers extends RecyclerView.ViewHolder {
        TextView login,pass,numtipe,dni;

        public ViewHolderUsers(@NonNull View itemView) {
            super(itemView);
            dni = (TextView) itemView.findViewById(R.id.dni);
            login = (TextView) itemView.findViewById(R.id.login);
            pass = (TextView) itemView.findViewById(R.id.pass);
            numtipe = (TextView) itemView.findViewById(R.id.numtipe);

        }
    }
}
