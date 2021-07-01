package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.CartModel;
import Model.OrderList;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.ViewHolder> {

    public List<OrderList> list;
    private Context context;
    private UserOrderAdapter.ListClickListener mListClickListener;
    private UserOrderAdapter.ListMessageClickListener mMessageListClickListener;

    private DatabaseReference databaseReferenceUncomplete;
    private DatabaseReference databaseReferenceComplete;

    public UserOrderAdapter(Context context, UserOrderAdapter.ListClickListener onListClickListener, UserOrderAdapter.ListMessageClickListener mMessageListClickListener) {
        this.context = context;
        this.mListClickListener = onListClickListener;

        this.mMessageListClickListener = mMessageListClickListener;
    }

    public void setList(List<OrderList> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_oder_item, parent, false);
        return new UserOrderAdapter.ViewHolder(view, mListClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserOrderAdapter.ViewHolder holder, int position) {
        OrderList orderList = list.get(position);
        holder.orderId.setText(orderList.getOrderId());

        holder.phone.setText(orderList.getPhone());
        holder.address.setText(orderList.getCurrentaddress());
        holder.totalprice.setText(orderList.getTotalprice());
        holder.deliveryType.setText(orderList.getDeliverytype());

        String status = orderList.getStatus();
        if (status.equals("Cooking")) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.checked.setVisibility(View.GONE);
            holder.status.setTextColor(context.getResources().getColor(R.color.greed));

        } else if (status.equals("Cooked")) {
             holder.checked.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
            holder.status.setTextColor(context.getResources().getColor(R.color.greed));
        }
        else {
            holder.progressBar.setVisibility(View.GONE);
            holder.checked.setVisibility(View.GONE);
            holder.status.setTextColor(context.getResources().getColor(R.color.greed));
        }
        holder.status.setText(orderList.getStatus());
    }


    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public interface ListClickListener {
        void onListClick(OrderList orderList);
    }

    public interface ListMessageClickListener {
        void onMessageListClick(OrderList orderList);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView orderId, status, phone, address, totalprice, deliveryType;
        private ProgressBar progressBar;
        private ImageView completed, message, delete, checked;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView, UserOrderAdapter.ListClickListener mListClickListener) {
            super(itemView);
            orderId = itemView.findViewById(R.id.userorder_item_list_orderId);
            status = itemView.findViewById(R.id.userorder_item_list_status);
            phone = itemView.findViewById(R.id.userorder_item_list_phone);
            address = itemView.findViewById(R.id.userorder_item_list_address);
            totalprice = itemView.findViewById(R.id.userorder_item_list_total);
            deliveryType = itemView.findViewById(R.id.userorder_item_deliverytype);
            progressBar = itemView.findViewById(R.id.userorder_item_prgress);
            message = itemView.findViewById(R.id.userorder_item_list_message);
            checked = itemView.findViewById(R.id.userorder_item_checkedCooked);
            linearLayout = itemView.findViewById(R.id.userclick_linerLayout);


            /// -------------Hiding Completed icon and Delete icon-------------------- ///


            // ------------- Click Management -------------------- ///


            message.setOnClickListener(this);
            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v.getId() == R.id.userclick_linerLayout) {

                mListClickListener.onListClick(list.get(pos));
            } else if (v.getId() == R.id.userorder_item_list_message) {
                mMessageListClickListener.onMessageListClick(list.get(pos));
            }
        }

    }


}
