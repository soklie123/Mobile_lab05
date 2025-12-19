package com.example.expense_tracker_app.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "expenses")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int localId;

    @DocumentId // This is a Firestore annotation, you can keep or remove it
    @SerializedName("id")
    private String id; // This is the Server/API ID

    private double amount;
    private String currency;
    private String category;
    private String remark;
    private String createdBy;
    private String description;
    private String date;

    @ServerTimestamp // This is a Firestore annotation, it won't work with a standard API
    private Date createdDate;

    private boolean isSynced;

    @Ignore
    public Expense(double amount,
                   String currency,
                   String category,
                   String remark,
                   String description,
                   String date,
                   String createdBy) {

        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.remark = remark;
        this.description = description;
        this.date = date;
        this.createdBy = createdBy;
        this.isSynced = false;
    }


    public Expense() {
        this.isSynced = false; // Default to not synced
    }


    // --- GETTERS & SETTERS ---
    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }


    // (A new getter/setter for the localId is required)
    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    // --- All other Getters and Setters remain the same ---
    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCategory() { return category; }
    public String getRemark() { return remark; }
    public String getCreatedBy() { return createdBy; }
    public Date getCreatedDate() { return createdDate; }
    public String getDescription() { return description; }
    public String getDate() { return date; }

    public void setId(String id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCategory(String category) { this.category = category; }
    public void setRemark(String remark) { this.remark = remark; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
}

