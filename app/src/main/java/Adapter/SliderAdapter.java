package Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodhive.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import Constants.ShimmerConstants;
import Model.FoodItems;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<FoodItems> mSliderItems;
    private OnClick onClick;

    public SliderAdapter(Context context, OnClick onClick) {
        this.context = context;
        this.onClick = onClick;
    }


    public void addItem(List<FoodItems> sliderItem) {
        this.mSliderItems = sliderItem;
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {


            FoodItems foodItems = mSliderItems.get(position);
            viewHolder.foodname.setText(foodItems.getName());

            Picasso.with(context).load(foodItems.getImguri()).fit().centerCrop().placeholder(ShimmerConstants.getShimmer()).into(viewHolder.foodpic);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onSlideClick(mSliderItems.get(position));
                }
            });

    }

    @Override
    public int getCount() {

        if(mSliderItems == null) return  0 ;
        return mSliderItems.size();
    }

    public interface OnClick {
        void onSlideClick(FoodItems foodItems);
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        private ImageView foodpic;
        private TextView foodname;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            foodname = itemView.findViewById(R.id.slider_name);
            foodpic = itemView.findViewById(R.id.slider_image);
            ;

        }


    }

}

