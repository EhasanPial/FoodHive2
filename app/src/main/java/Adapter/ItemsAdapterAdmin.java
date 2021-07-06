package Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.ArrayList;
import java.util.List;

import Constants.ShimmerConstants;
import Model.FoodItems;

public class ItemsAdapterAdmin extends RecyclerView.Adapter<ItemsAdapterAdmin.ViewHolder> implements Filterable {

    public List<FoodItems> list, listFilter;
    private final Context context;
    private final ListClickListener mListClickListener;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceAllfood;
    private final Boolean isAdmin;

    public ItemsAdapterAdmin(Context context, Boolean isAdmin, ListClickListener onListClickListener) {
        this.context = context;
        this.mListClickListener = onListClickListener;
        this.isAdmin = isAdmin;
    }

    public void setList(List<FoodItems> list) {
        this.list = list;
        this.listFilter = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_horizontal, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItems foodItems = listFilter.get(position);
        Picasso.with(context).load(foodItems.getImguri()).fit().centerCrop().placeholder(ShimmerConstants.getShimmer()).into(holder.foodimgeitems);
        holder.foodname.setText(foodItems.getName());
        holder.des.setText(foodItems.getDes());
        String TK = "TK";
        holder.price.setText(foodItems.getPrice() + TK);
        holder.ratingText.setText(foodItems.getRating());



        holder.ratingBar.setRating(Float.parseFloat(foodItems.getRating()));
        holder.foodtype.setText(foodItems.getType());


        Log.d("Image", foodItems.getImguri());
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems").child(foodItems.getType());
        databaseReferenceAllfood = FirebaseDatabase.getInstance().getReference("AllFood");


        if (isAdmin) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Do you want to delete this item?");
                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            databaseReference.child(foodItems.getItemkey()).removeValue();
                            databaseReferenceAllfood.child(foodItems.getItemkey()).removeValue();
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
        if (listFilter == null) return 0;
        return Math.min(listFilter.size(), 8);
    }


    public interface ListClickListener {
        void onListClick(FoodItems foodItems);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private final TextView foodname;
        private final TextView ratingText;
        private final TextView des;
        private final TextView price;
        private final TextView foodtype;
        private final ImageView foodimgeitems;
        private final AppCompatRatingBar ratingBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodname = itemView.findViewById(R.id.itemLayout_name_id);
            ratingText = itemView.findViewById(R.id.itemLayout_ratingText_id);
            des = itemView.findViewById(R.id.itemLayout_des_id);
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
            mListClickListener.onListClick(listFilter.get(pos));
        }


    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString().toLowerCase();
                if (listFilter.isEmpty()) {
                    listFilter = list;
                } else {
                    List<FoodItems> filteredList = new ArrayList<>();
                    for (FoodItems f : list) {
                        if (f.getName().toLowerCase().contains(charString) || f.getType().toLowerCase().contains(charString) || f.getDes().toLowerCase().contains(charString)) {
                            filteredList.add(f);
                        }
                    }

                    listFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                //noinspection unchecked
                //listFilter = (List<FoodItems>) results.values;
                notifyDataSetChanged();
            }


        };
    }

}
