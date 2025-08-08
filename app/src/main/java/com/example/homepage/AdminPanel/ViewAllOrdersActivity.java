package com.example.homepage.AdminPanel;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.example.homepage.Helper.pdfUtility;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homepage.Adapters.OrderListAdapter;
import com.example.homepage.Domain.OrderModel;
import com.example.homepage.Helper.pdfUtility;

import com.example.homepage.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewAllOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderListAdapter adapter;
    private List<OrderModel> orderList = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView pendingCountText, completedCountText, totalIncomeText;
//    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_orders);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        pendingCountText = findViewById(R.id.pendingCountText);
        completedCountText = findViewById(R.id.completedCountText);
        totalIncomeText = findViewById(R.id.totalIncomeText);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Orders");


        // Setup RecyclerView
        adapter = new OrderListAdapter(orderList, this);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(adapter);

        // Load all orders initially
        loadOrders(null);




// Add this in onCreate() after initializing views
        Button generatePdfBtn = findViewById(R.id.generatePdfBtn);
        generatePdfBtn.setOnClickListener(v -> {
            if (!orderList.isEmpty()) {
                pdfUtility.generateOrderReport(this, orderList);
            } else {
                Toast.makeText(this, "No orders to generate report", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadOrders(String statusFilter) {
        Query query = db.collection("orders");

        if (statusFilter != null && !statusFilter.isEmpty()) {
            query = query.whereEqualTo("status", statusFilter);
        }

        query.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    orderList.clear();
                    int pendingCount = 0;
                    int completedCount = 0;
                    double totalIncome = 0;

                    for (QueryDocumentSnapshot doc : value) {
                        OrderModel order = doc.toObject(OrderModel.class);
                        orderList.add(order);

                        if (order.getStatus().equals("pending")) {
                            pendingCount++;
                        } else {
                            completedCount++;
                            totalIncome += order.getTotalPrice();
                        }
                    }

                    adapter.updateList(orderList);
                    updateStats(pendingCount, completedCount, totalIncome);
                });
    }

    private void updateStats(int pendingCount, int completedCount, double totalIncome) {
        pendingCountText.setText(String.valueOf(pendingCount));
        completedCountText.setText(String.valueOf(completedCount));
        totalIncomeText.setText(String.format("$%.2f", totalIncome));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_all) {
            loadOrders(null);
            return true;
        } else if (id == R.id.filter_pending) {
            loadOrders("pending");
            return true;
        } else if (id == R.id.filter_completed) {
            loadOrders("completed");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}