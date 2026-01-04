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

    @DocumentId
    @SerializedName("id")
    private String id;

    private double amount;
    private String currency;
    private String category;
    private String remark;
    private String createdBy;
    private String description;
    private String date;

    @ServerTimestamp
    private Date createdDate;

    private boolean isSynced;

    // NEW: Add receipt image URL field
    @SerializedName("receiptImageUrl")
    private String receiptImageUrl;

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
        this.isSynced = false;
    }

    // --- GETTERS & SETTERS ---
    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCategory() { return category; }
    public String getRemark() { return remark; }
    public String getCreatedBy() { return createdBy; }
    public Date getCreatedDate() { return createdDate; }
    public String getDescription() { return description; }
    public String getDate() { return date; }

    // NEW: Getter and Setter for receiptImageUrl
    public String getReceiptImageUrl() { return receiptImageUrl; }
    public void setReceiptImageUrl(String receiptImageUrl) {
        this.receiptImageUrl = receiptImageUrl;
    }

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