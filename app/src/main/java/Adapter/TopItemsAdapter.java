package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
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

public class TopItemsAdapter extends RecyclerView.Adapter<TopItemsAdapter.ViewHolder>  {

    public List<FoodItems> list, listFilter;
    private Context context;
    private TopItemsAdapter.ListClickListener mListClickListener;
    private DatabaseReference databaseReference;


    public TopItemsAdapter(Context context , TopItemsAdapter.ListClickListener onListClickListener) {
        this.context = context;
        this.mListClickListener = onListClickListener;

    }

    public void setList(List<FoodItems> list) {
        this.list = list;
        this.listFilter = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_item_item_layout, parent, false);
        return new TopItemsAdapter.ViewHolder(view, mListClickListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TopItemsAdapter.ViewHolder holder, int position) {
        FoodItems foodItems = listFilter.get(position);

        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder().setDuration(1000)
                .setBaseAlpha(0.7f)
                .setHighlightAlpha(0.6f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        Picasso.with(context).load(foodItems.getImguri()).placeholder(shimmerDrawable).into(holder.foodimgeitems);

        if(foodItems.getIstop().equals("false"))
        {
            holder.isTop.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.isTop.setVisibility(View.VISIBLE);
        }

        holder.foodname.setText(foodItems.getName());
        holder.des.setText(foodItems.getDes());
        holder.price.setText(foodItems.getPrice() + " TK");
        holder.ratingText.setText(foodItems.getRating());
        holder.ratingBar.setRating(Float.parseFloat(foodItems.getRating()));
        holder.foodtype.setText(foodItems.getType());





    }

    @Override
    public int getItemCount() {
        if (listFilter == null) return 0;
        return listFilter.size();
    }


    public interface ListClickListener {
        void onListClick(FoodItems foodItems);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private TextView foodname, ratingText, des, price, foodtype;
        private ImageView foodimgeitems, isTop;
        private AppCompatRatingBar ratingBar;


        public ViewHolder(@NonNull View itemView, TopItemsAdapter.ListClickListener mListClickListener) {
            super(itemView);
            foodname = itemView.findViewById(R.id.topitem_name_id);
            ratingText = itemView.findViewById(R.id.topitem_ratingText_id);
            des = itemView.findViewById(R.id.topitem_des_id);
            foodtype = itemView.findViewById(R.id.topitem_type_id);
            price = itemView.findViewById(R.id.topitem_price_id);
            foodimgeitems = itemView.findViewById(R.id.topitem_img_id);
            ratingBar = itemView.findViewById(R.id.topitem_ratingBar_id);
            isTop = itemView.findViewById(R.id.topitem_make_upward_id);

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