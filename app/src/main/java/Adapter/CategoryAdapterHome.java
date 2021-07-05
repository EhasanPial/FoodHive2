package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodhive.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Constants.ShimmerConstants;
import Model.CategoryModel;

public class CategoryAdapterHome  extends RecyclerView.Adapter<CategoryAdapterHome.MyHolder> {

    private List<CategoryModel> categoryModelList;
    private CategoryAdapterHome.OnCatClick catClick;
    private Context context;

    public CategoryAdapterHome(Context context, CategoryAdapterHome.OnCatClick onCatClick) {
        this.catClick = onCatClick;
        this.context = context;
    }

    public void setCategoryModelList(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapterHome.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_circular, parent, false);
        return new CategoryAdapterHome.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapterHome.MyHolder holder, int position) {
        CategoryModel categoryModel = categoryModelList.get(position);
        holder.catname.setText(categoryModel.getCatname());


        Picasso.with(context).load(categoryModel.getCatimage()).fit().centerCrop().placeholder(ShimmerConstants.getShimmer()).into(holder.catimage);
    }

    @Override
    public int getItemCount() {
        if (categoryModelList == null) return 0;
        return categoryModelList.size();
    }

    public interface OnCatClick {
        void onCatClick(CategoryModel categoryModel);
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView catimage;
        private TextView catname;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            catname = itemView.findViewById(R.id.cat_name_home);
            catimage = itemView.findViewById(R.id.back_cat_img_home);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("catclick", "clicked");
            int pos = getAdapterPosition();
            catClick.onCatClick(categoryModelList.get(pos));
        }


    }


}
