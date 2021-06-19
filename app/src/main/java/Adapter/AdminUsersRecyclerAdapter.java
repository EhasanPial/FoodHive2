package Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.R;

import java.util.List;

import Model.UsersModel;

public class AdminUsersRecyclerAdapter extends RecyclerView.Adapter<AdminUsersRecyclerAdapter.ViewHolder> {

    private List<UsersModel> list;
    private Context context ;

    public AdminUsersRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<UsersModel> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_user_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         UsersModel usersModel = list.get(position)  ;
         holder.name.setText(usersModel.getName());
         holder.phone.setText(usersModel.getPhone());
         holder.address.setText(usersModel.getAddress());
         holder.orders.setText(usersModel.getTotalOrders());

         holder.phone.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(Intent.ACTION_DIAL) ;
                 intent.setData(Uri.parse("tel:"+usersModel.getPhone()));
                 context.startActivity(intent);
             }
         });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, phone, address, orders;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adapter_user_name_id);
            phone = itemView.findViewById(R.id.adapter_phone_id);
            address = itemView.findViewById(R.id.adapter_address_id);
            orders = itemView.findViewById(R.id.adapter_total_id);
        }
    }
}
