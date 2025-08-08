package com.example.homepage.Domain;

import java.util.List;

public class OrderModel {
    private String orderId;
    private String userId;
    private String userName;
    private List<PopularDomain> items;  // This should be List<popularDomain>
    private double totalPrice;
    private String status;
    private long timestamp;

    // Constructors, getters and setters
    public OrderModel() {} // Needed for Firestore

    public OrderModel(String orderId, String userId, String userName,
                      List<PopularDomain> items, double totalPrice, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters for all fields
    public List<PopularDomain> getItems() { return items; }
    public void setItems(List<PopularDomain> items) { this.items = items; }
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}