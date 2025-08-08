package com.example.homepage.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.R;
import java.util.ArrayList;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminViewHolder> {

    private List<PopularDomain> products;
    private final ProductClickListener editListener;
    private final ProductDeleteListener deleteListener;

    public AdminProductAdapter(List<PopularDomain> products,
                               ProductClickListener editListener,
                               ProductDeleteListener deleteListener) {
        this.products = new ArrayList<>(products);
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_admin_product, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        PopularDomain product = products.get(position);

        holder.title.setText(product.getTitle());
        holder.price.setText(String.format("$%.2f", product.getPrice()));

        if (product.getPicUrl().startsWith("https")) {
            Glide.with(holder.itemView)
                    .load(product.getPicUrl())
                    .into(holder.image);
        } else {
            int drawableId = holder.itemView.getResources()
                    .getIdentifier(product.getPicUrl(), "drawable",
                            holder.itemView.getContext().getPackageName());
            Glide.with(holder.itemView)
                    .load(drawableId)
                    .into(holder.image);
        }

        holder.editBtn.setOnClickListener(v -> editListener.onProductClick(product));
        holder.deleteBtn.setOnClickListener(v -> deleteListener.onProductDelete(product.getId()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<PopularDomain> newProducts) {
        products = new ArrayList<>(newProducts);
        notifyDataSetChanged();
    }

    public void updateProduct(PopularDomain updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(updatedProduct.getId())) {
                products.set(i, updatedProduct);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void removeProduct(String productId) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(productId)) {
                products.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView image, editBtn, deleteBtn;
        TextView title, price;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.adminProductImage);
            title = itemView.findViewById(R.id.adminProductTitle);
            price = itemView.findViewById(R.id.adminProductPrice);
            editBtn = itemView.findViewById(R.id.editProductBtn);
            deleteBtn = itemView.findViewById(R.id.deleteProductBtn);
        }
    }

    public interface ProductClickListener {
        void onProductClick(PopularDomain product);
    }

    public interface ProductDeleteListener {
        void onProductDelete(String productId);
    }
}