package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.FoodItems;
import Model.OrderList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    public List<OrderList> list;
    private Context context;
    private OrderListAdapter.ListClickListener mListClickListener;


    public OrderListAdapter(Context context, OrderListAdapter.ListClickListener onListClickListener) {
        this.context = context;
        this.mListClickListener = onListClickListener;
    }

    public void setList(List<OrderList> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_list, parent, false);
        return new OrderListAdapter.ViewHolder(view, mListClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, int position) {
        OrderList orderList = list.get(position);
        holder.orderId.setText(orderList.getOrderId());
        holder.status.setText(orderList.getStatus());
        holder.phone.setText(orderList.getPhone());
        holder.address.setText(orderList.getCurrentaddress());
        holder.totalprice.setText(orderList.getTotalprice());



    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public interface ListClickListener {
        void onListClick(OrderList orderList);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private TextView orderId, status, phone, address, totalprice ;


        public ViewHolder(@NonNull View itemView, OrderListAdapter.ListClickListener mListClickListener) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_item_list_orderId) ;
            status = itemView.findViewById(R.id.order_item_list_status) ;
            phone = itemView.findViewById(R.id.order_item_list_phone);
            address = itemView.findViewById(R.id.order_item_list_address);
            totalprice = itemView.findViewById(R.id.order_item_list_total);

            itemView.setOnClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {

            return false;
        }


        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mListClickListener.onListClick(list.get(pos));
        }
    }


}
