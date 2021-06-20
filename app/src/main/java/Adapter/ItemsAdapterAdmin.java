package Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import java.util.List;

import Model.FoodItems;
import Model.UsersModel;

public class ItemsAdapterAdmin extends RecyclerView.Adapter<ItemsAdapterAdmin.ViewHolder> {

    public List<FoodItems> list;
    private Context context;
    private ListClickListener mListClickListener;
    private DatabaseReference databaseReference;

    public ItemsAdapterAdmin(Context context, ListClickListener onListClickListener) {
        this.context = context;
        this.mListClickListener = onListClickListener;
    }

    public void setList(List<FoodItems> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_horizontal, parent, false);
        return new ViewHolder(view, mListClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItems foodItems = list.get(position);
        holder.foodname.setText(foodItems.getName());
        //  holder.des.setText(foodItems.);
        holder.price.setText(foodItems.getPrice());
        //  holder.ratingText.setText();
        holder.foodtype.setText(foodItems.getType());


        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems").child(foodItems.getType());


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Do you want to delete this category?");
                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseReference.child(foodItems.getItemkey()).removeValue();
                        notifyDataSetChanged();

                    }
                });

                builder.create().show();
                return false;

            }
        });


    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public interface ListClickListener {
        void onListClick(FoodItems s);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private TextView foodname, ratingText, des, price, foodtype;
        private ImageView foodimg;
        private AppCompatRatingBar ratingBar;
        private ListClickListener mListClickListener;

        public ViewHolder(@NonNull View itemView, ListClickListener mListClickListener) {
            super(itemView);
            foodname = itemView.findViewById(R.id.itemLayout_name_id);
            ratingText = itemView.findViewById(R.id.itemLayout_rating_id);
            des = itemView.findViewById(R.id.itemLayout_des_id);
            foodtype = itemView.findViewById(R.id.itemLayout_type_id);
            price = itemView.findViewById(R.id.itemLayout_price_id);
            foodimg = itemView.findViewById(R.id.itemLayout_img_id);
            ratingBar = itemView.findViewById(R.id.itemLayout_rating_id);
            this.mListClickListener = mListClickListener;

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
