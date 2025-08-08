package com.example.homepage.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homepage.AdminPanel.ViewAllOrdersActivity;
import com.example.homepage.Domain.OrderModel;
import com.example.homepage.Domain.PopularDomain;
import com.example.homepage.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private List<OrderModel> orderList;
    private FirebaseFirestore db;

    public OrderListAdapter(List<OrderModel> orderList, ViewAllOrdersActivity activity) {
        this.orderList = orderList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        // Set basic order information
        holder.orderIdText.setText("Order #" + order.getOrderId().substring(0, 8));
        holder.userText.setText("Customer: " + order.getUserName());
        holder.totalText.setText(String.format("Total: $%.2f", order.getTotalPrice()));
        holder.statusText.setText("Status: " + order.getStatus().toUpperCase());

        // Build and display ordered items with quantities
        StringBuilder itemsBuilder = new StringBuilder();
        if (order.getItems() != null) {
            for (PopularDomain item : order.getItems()) {
                itemsBuilder.append("• ")
                        .append(item.getTitle())
                        .append(" (Qty: ")
                        .append(item.getNumberinCart())
                        .append(")\n");
            }
        }
        holder.itemsText.setText(itemsBuilder.toString().trim());

        // Handle order confirmation
        if (order.getStatus().equals("pending")) {
            holder.confirmBtn.setVisibility(View.VISIBLE);
            holder.confirmBtn.setOnClickListener(v -> {
                db.collection("orders").document(order.getOrderId())
                        .update("status", "completed")
                        .addOnSuccessListener(aVoid -> {
                            order.setStatus("completed");
                            notifyItemChanged(position);
                        });
            });
        } else {
            holder.confirmBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateList(List<OrderModel> newList) {
        orderList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, userText, itemsText, totalText, statusText;
        Button confirmBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            userText = itemView.findViewById(R.id.userText);
            itemsText = itemView.findViewById(R.id.itemsText);
            totalText = itemView.findViewById(R.id.totalText);
            statusText = itemView.findViewById(R.id.statusText);
            confirmBtn = itemView.findViewById(R.id.confirmBtn);
        }
    }
}