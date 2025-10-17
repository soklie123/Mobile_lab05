package com.example.expense_tracker_app.models;

public class Expense {
    private long id;
    private final String name;
    private final double amount;
    private final String date;
    private final String description; // optional (details)

    public Expense(long id, String name, double amount, String date, String description) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Constructor without ID (for new expenses)
    public Expense(String name, double amount, String date, String description) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
}
