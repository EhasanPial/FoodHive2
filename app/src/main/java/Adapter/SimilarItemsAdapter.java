package Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import Model.FoodItems;
import Model.UsersModel;

public class SimilarItemsAdapter extends RecyclerView.Adapter<SimilarItemsAdapter.ViewHolder> {

    public List<FoodItems> list;
    private Context context;
    private ListClickListener mListClickListener;
    private DatabaseReference databaseReference;
    private  Boolean isAdmin  ;
    public SimilarItemsAdapter(Context context, Boolean isAdmin, ListClickListener onListClickListener) {
        this.context = context;
        this.mListClickListener = onListClickListener;
        this.isAdmin = isAdmin ;
    }

    public void setList(List<FoodItems> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.similar_item_horizontal, parent, false);
        return new ViewHolder(view, mListClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItems foodItems = list.get(position);
        holder.foodname.setText(foodItems.getName());
       // holder.des.setText(foodItems.getDes());
        holder.price.setText(foodItems.getPrice() + " TK");
        holder.ratingText.setText(foodItems.getRating());
        holder.ratingBar.setRating(Float.parseFloat(foodItems.getRating()));
        holder.foodtype.setText(foodItems.getType());
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setDuration(1000)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(0.6f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        Picasso.with(context).load(foodItems.getImguri()).placeholder(shimmerDrawable).into(holder.foodimgeitems);

        Log.d("Image", foodItems.getImguri());
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems").child(foodItems.getType());


        if(isAdmin) {
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


    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public interface ListClickListener {
        void onListClick(FoodItems foodItems);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private TextView foodname, ratingText, des, price, foodtype;
        private ImageView foodimgeitems;
        private AppCompatRatingBar ratingBar;


        public ViewHolder(@NonNull View itemView, ListClickListener mListClickListener) {
            super(itemView);
            foodname = itemView.findViewById(R.id.itemLayout_name_id);
            ratingText = itemView.findViewById(R.id.itemLayout_ratingText_id);
           // des = itemView.findViewById(R.id.itemLayout_des_id);
            foodtype = itemView.findViewById(R.id.itemLayout_type_id);
            price = itemView.findViewById(R.id.itemLayout_price_id);
            foodimgeitems = itemView.findViewById(R.id.itemLayout_img_id);
            ratingBar = itemView.findViewById(R.id.itemlayout_ratingBar_id);

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
