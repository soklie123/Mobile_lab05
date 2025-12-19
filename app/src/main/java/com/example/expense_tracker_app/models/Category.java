package com.example.expense_tracker_app.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.DocumentId;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "categories")
public class Category {

    // Local Room ID
    @PrimaryKey(autoGenerate = true)
    private int localId;

    // Remote / API / Firestore ID (optional)
    @DocumentId
    @SerializedName("id")
    private String id;

    // Category name (Food, Transport, etc.)
    private String name;

    // Sync flag (same pattern as Expense)
    private boolean isSynced;

    // -------- CONSTRUCTORS --------

    public Category() {
        this.isSynced = false;
    }

    @Ignore
    public Category(String name) {
        this.name = name;
        this.isSynced = false;
    }

    // -------- GETTERS & SETTERS --------

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
