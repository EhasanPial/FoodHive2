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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.CartModel;

public class ChatterAdapter extends RecyclerView.Adapter<ChatterAdapter.ViewHolder> {

    public List<CartModel> list;
    private Context context;
    private ListClickListener mListClickListener;

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
