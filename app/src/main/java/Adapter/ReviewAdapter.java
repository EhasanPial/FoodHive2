package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import Constants.BaseString;
import Model.ChatModel;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    public List<ChatModel> list;
    private Context context;

    private FirebaseAuth firebaseAuth;

    public ReviewAdapter(Context context) {
        this.context = context;

    }

    public void setList(List<ChatModel> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item_item_layout, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        ChatModel chatModel = list.get(position);

        firebaseAuth = FirebaseAuth.getInstance();
        long time = Long.parseLong(chatModel.getTimestamp());

        holder.date.setText(BaseString.getDateForReview(time));
        holder.msg.setText(chatModel.getMessage());
        holder.user.setText(chatModel.getSender());


    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView msg, user, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.review_item_msg_id);
            user = itemView.findViewById(R.id.review_item_user_id);
            date = itemView.findViewById(R.id.review_item_date_id);
        }


    }


}
