package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import Model.CartModel;


public class AdminOrderItemsAdapter extends RecyclerView.Adapter<AdminOrderItemsAdapter.ViewHolder> {

    public List<CartModel> list;
    private Context context;
    private ListClickListener mListClickListener;
    private DatabaseReference databaseReference, databaseReference1;
    private FirebaseAuth firebaseAuth;
    private String phone = "";
    private String TotalPrice = "";
    private int countPlus = 0, countMinus = 0;



    public AdminOrderItemsAdapter(Context context, ListClickListener onListClickListener) {
        this.context = context;
        this.mListClickListener = onListClickListener;

    }

    public void setList(List<CartModel> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orderitems_item_layout, parent, false);
        return new ViewHolder(view, mListClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        CartModel cartModel = list.get(position);
        holder.foodname.setText(cartModel.getFoodname());
        int singlePrice = Integer.parseInt(cartModel.getPrice());
        int quantity = Integer.parseInt(cartModel.getQuantity());
        holder.price.setText(singlePrice * quantity + " TK  ");
        holder.quantity.setText(cartModel.getQuantity()+"x");

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("CartItems");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Cart").child(firebaseAuth.getCurrentUser().getUid());




    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public interface ListClickListener {
        void onListClick(CartModel cartModel);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView foodname, price, quantity;
         

        public ViewHolder(@NonNull View itemView, AdminOrderItemsAdapter.ListClickListener mListClickListener) {
            super(itemView);
            foodname = itemView.findViewById(R.id.orderItems_item_name);
            price = itemView.findViewById(R.id.orderItems_item_price);
            quantity = itemView.findViewById(R.id.orderItems_item_quantity) ;


            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mListClickListener.onListClick(list.get(pos));
        }
    }


}
