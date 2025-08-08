package com.example.homepage.Adapters;

import android.content.Context;
import android.content.Intent;
import android.widget.Filter;
import android.widget.Filterable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.example.homepage.Activity.DetailActivity;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.R;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    private List<PopularDomain> originalList;
    private List<PopularDomain> filteredList;
    private Context context;

    public ProductAdapter(ArrayList<PopularDomain> items) {
        this.originalList = new ArrayList<>(items);
        this.filteredList = new ArrayList<>(items);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_pop_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PopularDomain item = filteredList.get(position);

        holder.titleTxt.setText(item.getTitle());
        holder.feeTxt.setText(String.format("$%.2f", item.getPrice()));
        holder.ScoreTxt.setText(String.valueOf(item.getScore()));
        holder.reviewTxt.setText(String.format("(%d reviews)", item.getReview()));

        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl())
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public long getItemId(int position) {
        return filteredList.get(position).hashCode();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<PopularDomain> filteredItems = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredItems.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (PopularDomain item : originalList) {
                        if (item.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredItems.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredItems;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results.values != null) {
                    filteredList.addAll((List<PopularDomain>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    public void updateProducts(ArrayList<PopularDomain> newProducts) {
        originalList = new ArrayList<>(newProducts);
        filteredList = new ArrayList<>(newProducts);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, feeTxt, ScoreTxt, reviewTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            feeTxt = itemView.findViewById(R.id.feeTxt);
            ScoreTxt = itemView.findViewById(R.id.scoreTxt);
            reviewTxt = itemView.findViewById(R.id.reviewTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}








//package com.example.homepage.Adapters;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
//import com.example.homepage.Activity.DetailActivity;
//import com.example.homepage.Domain.PopularDomain;
//import com.example.homepage.R;
//
//import java.util.ArrayList;
//
//public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
//
//    private ArrayList<PopularDomain> items;
//    private Context context;
//
//    public ProductAdapter(ArrayList<PopularDomain> items) {
//        this.items = items;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_pop_list, parent, false);
//        context = parent.getContext();
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        PopularDomain item = items.get(position);
//
//        holder.titleTxt.setText(item.getTitle());
//        holder.feeTxt.setText(String.format("$%.2f", item.getPrice()));
//        holder.ScoreTxt.setText(String.valueOf(item.getScore()));
//        holder.reviewTxt.setText(String.format("(%d reviews)", item.getReview()));
//
//        // Load image using Glide with Cloudinary URL
//        Glide.with(holder.itemView.getContext())
//                .load(item.getPicUrl())
//                .transform(new GranularRoundedCorners(30, 30, 0, 0))
//                .placeholder(R.drawable.ic_baseline_person_24) // Add placeholder if needed
//                .error(R.drawable.ic_baseline_person_24) // Add error image if needed
//                .into(holder.pic);
//
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, DetailActivity.class);
//            intent.putExtra("object", item);
//            context.startActivity(intent);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView titleTxt, feeTxt, ScoreTxt, reviewTxt;
//        ImageView pic;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            titleTxt = itemView.findViewById(R.id.titleTxt);
//            feeTxt = itemView.findViewById(R.id.feeTxt);
//            ScoreTxt = itemView.findViewById(R.id.scoreTxt);
//            reviewTxt = itemView.findViewById(R.id.reviewTxt);
//            pic = itemView.findViewById(R.id.pic);
//        }
//    }
//}