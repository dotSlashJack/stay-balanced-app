package edu.staybalanced.staybalanced;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IconAdapter  extends RecyclerView.Adapter<IconAdapter.IconViewHolder>{

    List<IconItem> itemList;
    ImageView selectedIcon;
    TextView iconName;

    public IconAdapter(List<IconItem> itemList, ImageView selectedIcon, TextView iconName) {
        this.itemList = itemList;
        this.selectedIcon = selectedIcon;
        this.iconName = iconName;
    }

    public class IconViewHolder extends RecyclerView.ViewHolder {
        ImageButton img;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.add_icon_button);
        }
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_icon_recycler_card, parent, false);
        IconViewHolder holder = new IconViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int index) {
        holder.img.setImageResource(itemList.get(index).getImgId());
        holder.img.setOnClickListener(view -> {
            selectedIcon.setVisibility(View.VISIBLE);
            IconItem iconItem = itemList.get(holder.getAdapterPosition());
            selectedIcon.setImageResource(iconItem.getImgId());
            iconName.setText(iconItem.getName());
        });
    }

    @Override
    public int getItemCount() { return itemList.size(); }
}