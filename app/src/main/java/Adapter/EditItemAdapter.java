package Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import Model.UsersModel;

public class EditItemAdapter extends RecyclerView.Adapter<EditItemAdapter.ViewHolder> {

    public List<String> list;
    private Context context;
    private ListClickListener mListClickListener;
    private DatabaseReference databaseReference;

    public EditItemAdapter(Context context, ListClickListener onListClickListener) {
        this.context = context;
        this.mListClickListener = onListClickListener;
    }

    public void setList(List<String> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_edit_item_adapter_layout, parent, false);
        return new ViewHolder(view, mListClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String s = list.get(position);
        holder.name.setText(s);


        databaseReference = FirebaseDatabase.getInstance().getReference("FoodItems");

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
                        databaseReference.child(s).removeValue();
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
        void onListClick(String s);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private TextView name;


        public ViewHolder(@NonNull View itemView, ListClickListener mListClickListener) {
            super(itemView);
            name = itemView.findViewById(R.id.edit_item_adapter_textView);
            itemView.setOnClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {

            return false;
        }


        @Override
        public void onClick(View v) {
            Log.d("adapter","clicked") ;
            int pos = getAdapterPosition();
            mListClickListener.onListClick(list.get(pos));
        }
    }
}
