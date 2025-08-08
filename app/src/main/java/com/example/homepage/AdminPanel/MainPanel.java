package com.example.homepage.AdminPanel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homepage.Activity.LoginActivity;
import com.example.homepage.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainPanel extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private BarChart orderChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        LinearLayout addItemLayout = findViewById(R.id.addItemLayout);
        LinearLayout ViewAllItemLayout = findViewById(R.id.ViewAll);
        LinearLayout logoutLayout = findViewById(R.id.logoutadmin);
        LinearLayout profileLayout = findViewById(R.id.adminprofile);
        LinearLayout ViewAllOrdersLayout = findViewById(R.id.viewallorders);

        TextView pendingTextView = findViewById(R.id.pending);
        TextView completedTextView = findViewById(R.id.completed);
        TextView incomeTextView = findViewById(R.id.wholetime);
        orderChart = findViewById(R.id.pieChart); // Make sure this ID matches your layout

        // Set click listeners
        addItemLayout.setOnClickListener(v -> startActivity(new Intent(this, AddItemActivity.class)));
        profileLayout.setOnClickListener(v -> startActivity(new Intent(this, AdminProfileActivity.class)));
        ViewAllItemLayout.setOnClickListener(v -> startActivity(new Intent(this, AdminViewAllActivity.class)));
        ViewAllOrdersLayout.setOnClickListener(v -> startActivity(new Intent(this, ViewAllOrdersActivity.class)));
        logoutLayout.setOnClickListener(v -> logoutUser());

        // Configure chart
        setupChart();

        // Load data
        loadOrderData(pendingTextView, completedTextView, incomeTextView);
    }

    private void setupChart() {
        // Basic chart configuration
        orderChart.getDescription().setEnabled(true);
        orderChart.getDescription().setText(".");
        orderChart.setDrawValueAboveBar(true);
        orderChart.setPinchZoom(false);
        orderChart.setDrawGridBackground(false);
        orderChart.getLegend().setEnabled(false);

        // Configure X axis
        XAxis xAxis = orderChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(2);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Pending", "Completed"}));

        // Configure Y axis
        orderChart.getAxisLeft().setDrawGridLines(true);
        orderChart.getAxisRight().setEnabled(false);
    }

    private void loadOrderData(TextView pendingView, TextView completedView, TextView incomeView) {
        db.collection("orders").addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;

            int pendingCount = 0;
            int completedCount = 0;
            double totalIncome = 0.0;

            for (QueryDocumentSnapshot doc : value) {
                String status = doc.getString("status");
                Double totalPrice = doc.getDouble("totalPrice");

                if ("pending".equals(status)) {
                    pendingCount++;
                } else if ("completed".equals(status) && totalPrice != null) {
                    completedCount++;
                    totalIncome += totalPrice;
                }
            }

            // Update TextViews
            pendingView.setText(String.valueOf(pendingCount));
            completedView.setText(String.valueOf(completedCount));
            incomeView.setText(String.format("$%.2f", totalIncome));

            // Update chart with two bars
            updateChart(pendingCount, completedCount);
        });
    }

    private void updateChart(int pendingCount, int completedCount) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, pendingCount)); // Pending orders
        entries.add(new BarEntry(1, completedCount)); // Completed orders

        BarDataSet dataSet = new BarDataSet(entries, "Orders");
        dataSet.setColors(new int[]{Color.RED, Color.GREEN}); // Red for pending, green for completed
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.4f); // Set custom bar width

        orderChart.setData(data);
        orderChart.invalidate(); // Refresh chart
        orderChart.animateY(1000); // Add animation
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}