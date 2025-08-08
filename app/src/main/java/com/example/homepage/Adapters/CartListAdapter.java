package com.example.homepage.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.Helper.ChangeNumberItemsListener;
import com.example.homepage.Helper.ManagmentCart;
import com.example.homepage.R;
import java.util.ArrayList;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {
    ArrayList<PopularDomain> listItemSelected;
    private ManagmentCart managmentCart;
    ChangeNumberItemsListener changeNumberItemsListener;

    public CartListAdapter(ArrayList<PopularDomain> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PopularDomain item = listItemSelected.get(position);

        holder.title.setText(item.getTitle());
        holder.feeEachItem.setText("$" + item.getPrice());
        holder.totalEachItem.setText("$" + Math.round(item.getNumberinCart() * item.getPrice()));
        holder.num.setText(String.valueOf(item.getNumberinCart()));

        // Enhanced image loading for both URL and drawable
        if (item.getPicUrl().startsWith("http://") || item.getPicUrl().startsWith("https://")) {
            // Load from Cloudinary URL
            Glide.with(holder.itemView.getContext())
                    .load(item.getPicUrl())
                    .transform(new GranularRoundedCorners(30, 30, 30, 30))
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(holder.pic);
        } else {
            // Load from local drawable
            int drawableResourceId = holder.itemView.getContext().getResources()
                    .getIdentifier(item.getPicUrl(), "drawable", holder.itemView.getContext().getPackageName());

            if (drawableResourceId != 0) {
                Glide.with(holder.itemView.getContext())
                        .load(drawableResourceId)
                        .transform(new GranularRoundedCorners(30, 30, 30, 30))
                        .into(holder.pic);
            } else {
                // Fallback if drawable not found
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.ic_baseline_person_24)
                        .transform(new GranularRoundedCorners(30, 30, 30, 30))
                        .into(holder.pic);
            }
        }

        // Existing quantity controls
        holder.plusItem.setOnClickListener(v -> {
            managmentCart.plusNumberItem(listItemSelected, position, new ChangeNumberItemsListener() {
                @Override
                public void change() {
                    notifyDataSetChanged();
                    changeNumberItemsListener.change();
                }
            });
        });

        holder.minusItem.setOnClickListener(v -> {
            managmentCart.minusNumberItem(listItemSelected, position, new ChangeNumberItemsListener() {
                @Override
                public void change() {
                    notifyDataSetChanged();
                    changeNumberItemsListener.change();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem, plusItem, minusItem;
        ImageView pic;
        TextView totalEachItem, num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            plusItem = itemView.findViewById(R.id.PlusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            num = itemView.findViewById(R.id.numberItemTxt);
        }
    }
}