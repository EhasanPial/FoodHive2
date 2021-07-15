package Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.FcmNotificationsSender;
import com.example.foodhive.MainActivity;
import com.example.foodhive.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.BaseString;
import Model.CartModel;
import Model.FoodItems;
import Model.OrderList;
import UI.NotificationUser;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    public List<OrderList> list;
    private Context context;
    private Activity activity;
    private OrderListAdapter.ListClickListener mListClickListener;
    private OrderListAdapter.ListMessageClickListener mMessageListClickListener;
    private boolean isCompleted;
    private DatabaseReference databaseReferenceUncomplete;
    private DatabaseReference databaseReferenceComplete;
    private DatabaseReference databaseReferenceUsersOrder;

    public OrderListAdapter(Context context, boolean isCompleted, OrderListAdapter.ListClickListener onListClickListener, OrderListAdapter.ListMessageClickListener mMessageListClickListener, Activity activity) {
        this.context = context;
        this.mListClickListener = onListClickListener;
        this.isCompleted = isCompleted;
        this.mMessageListClickListener = mMessageListClickListener;
        this.activity = activity;
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
        Log.d("delivery", orderList.getDeliverytype() + "");
        holder.orderId.setText(orderList.getOrderId());
        holder.status.setText(orderList.getStatus());


        /// ---------- Setting progerss bar and checked imagee -- //

        String status = orderList.getStatus();
        if (status.equals(context.getString(R.string.accepted))) {
            holder.progressBar.setVisibility(View.GONE);
            holder.checked.setVisibility(View.VISIBLE);


        } else if (status.equals(context.getString(R.string.cooking_))) {
            holder.checked.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.VISIBLE);

        } else if (status.equals(context.getString(R.string.ready_for_delivery))) {
            holder.checked.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);

        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.checked.setVisibility(View.GONE);

        }

   /*     if (orderList.getStatus().equals("Cooked")) {
            holder.checked.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        } else if (orderList.getStatus().equals("Cooking")) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.checked.setVisibility(View.GONE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.checked.setVisibility(View.GONE);
        }
*/
        // ------------------------------------------- //


        holder.phone.setText(orderList.getPhone());
        holder.address.setText(orderList.getCurrentaddress());
        holder.totalprice.setText(orderList.getTotalprice() + " TK");
        holder.deliveryType.setText(orderList.getDeliverytype() + "");
        holder.username.setText(orderList.getUsername());
        long longtime = Long.parseLong(orderList.getTimestamp());

        holder.date.setText(BaseString.getDate(longtime));


        databaseReferenceUncomplete = FirebaseDatabase.getInstance().getReference().child("Order");
        databaseReferenceComplete = FirebaseDatabase.getInstance().getReference("CompletedOrder");
        databaseReferenceUsersOrder = FirebaseDatabase.getInstance().getReference().child("Users Order");


        // ---------------- Complete ----------------- //
        holder.completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Is this order completed?");
                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        putDataInDataBase(orderList);
                        notifyDataSetChanged();

                        NotificationUser notificationUser = new NotificationUser(context, orderList.getUid(), activity);
                        notificationUser.setFirebaseOrderNotification("Food Hive", "How was the food? \nPlease give us a review." );

                    }
                });

                builder.create().show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                databaseReferenceUncomplete.child(orderList.getOrderId()).removeValue();
                return false;
            }
        });

        // ------------------- Delete -------------------------- //

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Do you want to delete this order?");
                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseReferenceUncomplete.child(orderList.getOrderId()).removeValue();
                        Map<String, Object> m = new HashMap<>();
                        m.put("status", context.getString(R.string.order_is_canceled));

                        // ----------- Notification for delete ------------------ //
                        NotificationUser notificationUser = new NotificationUser(context, orderList.getUid(), activity);
                        notificationUser.setFirebaseOrderNotification("Food Hive", context.getString(R.string.Sorry_Duetosomereasonyourorderiscanceled));

                        databaseReferenceUsersOrder.child(orderList.getUid()).child(orderList.getOrderId()).child("others").updateChildren(m);

                        notifyDataSetChanged();

                    }
                });

                builder.create().show();

            }
        });


    }

    private void putDataInDataBase(OrderList orderList) {
        databaseReferenceUncomplete.child(orderList.getOrderId()).child("cartItems").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    CartModel cartModel = (d.getValue(CartModel.class));
                    databaseReferenceComplete.child(orderList.getOrderId()).child("cartItems").child(cartModel.getItemkey()).setValue(cartModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReferenceComplete.child(orderList.getOrderId()).child("others").setValue(orderList);
        Map<String, Object> m = new HashMap<>();
        m.put("status", "Completed");
        databaseReferenceComplete.child(orderList.getOrderId()).child("others").updateChildren(m);
        databaseReferenceUncomplete.child(orderList.getOrderId()).removeValue();
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

        private TextView orderId, status, phone, address, totalprice, date, username;
        private TextView deliveryType;
        private ImageView completed, message, delete;
        private LinearLayout linearLayout;

        private ImageView checked;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView, OrderListAdapter.ListClickListener mListClickListener) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_item_list_orderId);
            status = itemView.findViewById(R.id.order_item_list_status);
            phone = itemView.findViewById(R.id.order_item_list_phone);
            address = itemView.findViewById(R.id.order_item_list_address);
            totalprice = itemView.findViewById(R.id.order_item_list_total);
            completed = itemView.findViewById(R.id.order_item_list_completed);
            linearLayout = itemView.findViewById(R.id.click_linerLayout);
            message = itemView.findViewById(R.id.order_item_list_message);
            delete = itemView.findViewById(R.id.order_item_list_delete);
            deliveryType = itemView.findViewById(R.id.order_item_deliverytype);
            checked = itemView.findViewById(R.id.order_item_checkedCooked);
            progressBar = itemView.findViewById(R.id.order_item_prgress);
            date = itemView.findViewById(R.id.order_item_list_date);
            username = itemView.findViewById(R.id.order_item_userName);


            /// -------------Hiding Completed icon and Delete icon-------------------- ///

            if (isCompleted) {
                completed.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                checked.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);


            } else {
                status.setTextColor(context.getResources().getColor(R.color.greed));
            }

            // ------------- Click Management -------------------- ///
            linearLayout.setOnClickListener(this);
            message.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v.getId() == R.id.click_linerLayout) {

                mListClickListener.onListClick(list.get(pos));
            } else if (v.getId() == R.id.order_item_list_message) {
                mMessageListClickListener.onMessageListClick(list.get(pos));
            }
        }

    }


}
