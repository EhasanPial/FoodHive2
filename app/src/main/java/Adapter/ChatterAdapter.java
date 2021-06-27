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
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Model.CartModel;

public class ChatterAdapter extends RecyclerView.Adapter<ChatterAdapter.ViewHolder> {

    public List<CartModel> list;
    private Context context;
    private ListClickListener mListClickListener;
    private DatabaseReference databaseReference, databaseReference1;
    private FirebaseAuth firebaseAuth ;
    private String phone = "";
    private String TotalPrice = "";
    private int countPlus = 0, countMinus = 0;


    public void setPhone(String phone, String totalPrice) {
        this.phone = phone;
        TotalPrice = totalPrice;
        Log.d("PRICE1", TotalPrice + "");
    }

    public ChatterAdapter(Context context, ListClickListener onListClickListener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatter_item, parent, false);
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
        holder.numberText.setText(cartModel.getQuantity());

        firebaseAuth = FirebaseAuth.getInstance() ;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("CartItems");
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Cart").child(firebaseAuth.getCurrentUser().getUid());


        Map<String, Object> m = new HashMap<>();

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++countPlus;
                m.put("quantity", quantity + countPlus + "");
                holder.numberText.setText(quantity + countPlus + "");
                databaseReference.child(cartModel.getItemkey()).updateChildren(m);
                Log.d("PRICE2", TotalPrice + "");
                int Total = Integer.parseInt(TotalPrice);
                Total += countPlus * singlePrice;

                databaseReference1.child("TotalPrice").setValue(Total + "");


            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++countMinus;
                if (quantity >= countMinus) {
                    m.put("quantity", quantity - countMinus + "");
                    holder.numberText.setText(quantity - countMinus + "");
                    databaseReference.child(cartModel.getItemkey()).updateChildren(m);
                    int Total = Integer.parseInt(TotalPrice);
                    Total -= countMinus * singlePrice;
                    databaseReference1.child("TotalPrice").setValue(Total + "");
                }

            }
        });
        countPlus = 0;
        countMinus = 0;


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
        private TextView foodname, price, numberText;
        private ImageView plus, minus;

        public ViewHolder(@NonNull View itemView, ListClickListener mListClickListener) {
            super(itemView);
            foodname = itemView.findViewById(R.id.chatter_item_name);
            price = itemView.findViewById(R.id.chatter_item_price);
            numberText = itemView.findViewById(R.id.number_counter);
            plus = itemView.findViewById(R.id.imgPlus);
            minus = itemView.findViewById(R.id.imgMinus);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mListClickListener.onListClick(list.get(pos));
        }
    }


}
