package com.techtitudetribe.yummy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class HomeSliderItemAdapter extends RecyclerView.Adapter<HomeSliderItemAdapter.SliderViewHolder> {

    private List<HomeSliderItem> sliderItems;
    private ViewPager2 viewPager2;

    public HomeSliderItemAdapter(List<HomeSliderItem> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.home_viewpager_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));
        if (position == sliderItems.size()-2)
        {
            viewPager2.post(sliderRunnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        View mView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        void setImage(HomeSliderItem homeSliderItem)
        {
            ImageView imageView = mView.findViewById(R.id.home_viewpager_image);
            imageView.setImageResource(homeSliderItem.getImage());
        }
    }
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

}
