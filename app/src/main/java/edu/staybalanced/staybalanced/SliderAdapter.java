package edu.staybalanced.staybalanced;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{
    List<UnlockedAssets> sliderItems;
    ViewPager2 viewPager;

    SliderAdapter(List<UnlockedAssets> sliderItems, ViewPager2 viewPager) {
        this.sliderItems = sliderItems;
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public SliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_card, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.SliderViewHolder holder, int position) {
        if (sliderItems.get(position).getUnlocked()) {
            holder.imageView.setImageResource(sliderItems.get(position).getImage());
        } else {
            holder.imageView.setImageResource(R.drawable.locked);
        }
        holder.descriptionView.setText(sliderItems.get(position).getDescription());
        if (position == sliderItems.size() - 2) {
            viewPager.post(scroll_runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionView;
        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ic_image_holder);
            descriptionView = itemView.findViewById(R.id.ic_text_holder);
        }
    }

    // infinite scrolling roulette
    private Runnable scroll_runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };
}
