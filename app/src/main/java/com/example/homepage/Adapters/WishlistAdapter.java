package com.example.homepage.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.homepage.Activity.DetailActivity;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.Helper.WishlistHelper;
import com.example.homepage.R;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final Context context;
    private final List<PopularDomain> wishlistItems;

    public WishlistAdapter(Context context, List<PopularDomain> wishlistItems) {
        this.context = context;
        this.wishlistItems = wishlistItems;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        PopularDomain item = wishlistItems.get(position);

        holder.titleTxt.setText(item.getTitle());
        holder.feeEachItem.setText(String.format("$%.2f", item.getPrice()));
        holder.totalEachItem.setText(String.format("$%.2f", item.getPrice()));

        // Enhanced image loading for both URL and drawable
        if (item.getPicUrl().startsWith("http://") || item.getPicUrl().startsWith("https://")) {
            // Load from Cloudinary URL
            Glide.with(context)
                    .load(item.getPicUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(holder.pic);
        } else {
            // Load from local drawable
            int drawableResourceId = holder.itemView.getResources()
                    .getIdentifier(item.getPicUrl(), "drawable", context.getPackageName());

            if (drawableResourceId != 0) {
                Glide.with(context)
                        .load(drawableResourceId)
                        .into(holder.pic);
            } else {
                // Fallback if drawable not found
                Glide.with(context)
                        .load(R.drawable.ic_baseline_person_24)
                        .into(holder.pic);
            }
        }

        // Existing click listeners remain unchanged
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);
        });

        holder.removeBookmarkBtn.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if(currentPosition != RecyclerView.NO_POSITION){
                wishlistItems.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                WishlistHelper.saveWishlist(context, wishlistItems);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, feeEachItem, totalEachItem;
        ImageView pic, removeBookmarkBtn;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            pic = itemView.findViewById(R.id.pic);
            removeBookmarkBtn = itemView.findViewById(R.id.removeBookmarkBtn);
        }
    }
}