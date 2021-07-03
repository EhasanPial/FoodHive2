package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import Constants.BaseString;
import Model.ChatModel;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public List<ChatModel> list;
    private Context context;

    private FirebaseAuth firebaseAuth;

    public ChatAdapter(Context context) {
        this.context = context;

    }

    public void setList(List<ChatModel> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatModel chatModel = list.get(position);

        firebaseAuth = FirebaseAuth.getInstance();

        String sendID = chatModel.getSender();
        long time = Long.parseLong(chatModel.getTimestamp());
        // ----------- Right msg --------- //
        if (sendID.equals(firebaseAuth.getCurrentUser().getUid())) {
            holder.linearLayoutright.setVisibility(View.VISIBLE);
            holder.linearLayoutleft.setVisibility(View.INVISIBLE);
            holder.leftdate.setVisibility(View.INVISIBLE);
            holder.rightmsg.setText(chatModel.getMessage());
            holder.rightdate.setText(BaseString.getDate(time));

        }
        // ----------- left msg --------- //
        else {

            holder.linearLayoutleft.setVisibility(View.VISIBLE);
            holder.linearLayoutright.setVisibility(View.INVISIBLE);
            holder.rightdate.setVisibility(View.INVISIBLE);
            holder.leftmsg.setText(chatModel.getMessage());
            holder.leftdate.setText(BaseString.getDate(time));

        }


    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView rightmsg, leftmsg, leftdate, rightdate;
        private LinearLayout linearLayoutright, linearLayoutleft;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rightmsg = itemView.findViewById(R.id.right_chat_item);
            leftmsg = itemView.findViewById(R.id.left_chat_item);
            linearLayoutleft = itemView.findViewById(R.id.left_chat_item_layout);
            linearLayoutright = itemView.findViewById(R.id.right_chat_item_layout);
            leftdate = itemView.findViewById(R.id.left_chat_item_date);
            rightdate = itemView.findViewById(R.id.right_chat_item_date);


        }


    }


}
