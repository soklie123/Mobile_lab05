package com.example.expense_tracker_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.models.Expense;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    // -------- CLICK INTERFACE --------
    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    private final List<Expense> expenseList;
    private final OnExpenseClickListener listener;

    // -------- CONSTRUCTOR --------
    public ExpenseAdapter(List<Expense> expenseList, OnExpenseClickListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    // -------- UPDATE LIST --------
    public void setExpenseList(List<Expense> newExpenseList) {
        this.expenseList.clear();
        this.expenseList.addAll(newExpenseList);
        notifyDataSetChanged();
    }

    // -------- INFLATE ITEM LAYOUT --------
    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    // -------- BIND DATA TO UI (Corrected Version) --------
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position)  {
        Expense expense = expenseList.get(position);

        // ---- Bind title and amount ----
        holder.tvName.setText(expense.getRemark());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", expense.getAmount()));

        // ---- Date parsing ----
        String displayDate = "N/A";
        if (expense.getDate() != null && !expense.getDate().isEmpty()) {
            String[] possibleFormats = {
                    "yyyy-MM-dd HH:mm:ss",
                    "d MMM yyyy, HH:mm:ss",
                    "MMM dd, yyyy HH:mm",
                    "MM/dd/yyyy HH:mm"
            };
            for (String pattern : possibleFormats) {
                try {
                    SimpleDateFormat parser = new SimpleDateFormat(pattern, Locale.getDefault());
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                    displayDate = displayFormat.format(parser.parse(expense.getDate()));
                    break;
                } catch (Exception ignored) {}
            }
        }
        holder.tvDate.setText(displayDate);

        // ---- Category icon & background ----
        Context context = holder.itemView.getContext();
        int iconResId, iconTint, backgroundTint;

        switch (expense.getCategory().toLowerCase()) {
            case "food":
                iconResId = R.drawable.ic_food;
                iconTint = R.color.cat_food_icon;
                backgroundTint = R.color.cat_food_bg;
                break;
            case "transport":
                iconResId = R.drawable.ic_transport;
                iconTint = R.color.cat_transport_icon;
                backgroundTint = R.color.cat_transport_bg;
                break;
            case "shopping":
                iconResId = R.drawable.ic_shopping;
                iconTint = R.color.cat_shopping_icon;
                backgroundTint = R.color.cat_shopping_bg;
                break;
            case "drink":
                iconResId = R.drawable.ic_drink;
                iconTint = R.color.cat_drink_icon;
                backgroundTint = R.color.cat_drink_bg;
                break;
            default: // Handles new user-added categories
                iconResId = R.drawable.ic_other;
                iconTint = R.color.cat_default_icon;
                backgroundTint = R.color.cat_default_bg;
                break;
        }

        holder.ivCategoryIcon.setImageResource(iconResId);
        holder.ivCategoryIcon.setColorFilter(ContextCompat.getColor(context, iconTint));
        holder.iconContainer.setCardBackgroundColor(ContextCompat.getColor(context, backgroundTint));

        // ---- Click listener ----
        holder.itemView.setOnClickListener(v -> listener.onExpenseClick(expense));
    }



    // -------- ITEM COUNT --------
    @Override
    public int getItemCount() {
        return expenseList != null ? expenseList.size() : 0;
    }

    // -------- VIEW HOLDER (Corrected Version) --------
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvDate;
        ImageView ivCategoryIcon;
        // Add a reference to the container card
        MaterialCardView iconContainer;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExpenseTitle);
            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            // Initialize the container card
            iconContainer = itemView.findViewById(R.id.icon_container);
        }
    }
}
