package com.example.promote;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ShopViewHolder> {

    private Context context;
    private List<Shop> shopList;

    // Listener for handling item click events
    private static OnItemClickListener mListener;

    public ShopListAdapter(Context context, List<Shop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    // Creates the view holder for each shop item in the list
    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_shop_item, parent, false);
        return new ShopViewHolder(view);
    }

    // Binds the data from the shop list to the view holder
    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shopList.get(position);
        holder.shopNameTextView.setText(shop.getName());

        // Load the image using Glide if a URL is available
        if (shop.getImageUrl() != null) {
            Glide.with(context)
                    .load(shop.getImageUrl())
                    .into(holder.shopImageView);
        } else {
            // Set a default image or hide the ImageView if no image is available
            holder.shopImageView.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    // Returns the number of items in the shop list
    @Override
    public int getItemCount() {
        return shopList.size();
    }

    // Set the click listener for handling item clicks
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // Interface for defining the item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ViewHolder class to hold the views for each shop item
    public class ShopViewHolder extends RecyclerView.ViewHolder {
        public ImageView shopImageView;
        public TextView shopNameTextView;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            shopImageView = itemView.findViewById(R.id.shop_image_view);
            shopNameTextView = itemView.findViewById(R.id.shop_name_text_view);

            // Set the click listener for the entire item view
            itemView.setOnClickListener(view -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
