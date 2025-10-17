package com.example.expense_tracker_app.services;

import com.example.expense_tracker_app.models.Expense;
import java.util.ArrayList;
import java.util.List;

public class ExpenseService {
    // Simplified In-Memory Storage - Replace with Room/SQLite for a real app
    private static final List<Expense> expenses = new ArrayList<>();
    private static long nextId = 1;

    // Static initializer to populate initial data with the new model fields
    static {
        // --- Sample Food Expenses ---
        expenses.add(new Expense(nextId++, "Breakfast", 5.0, "2025-10-15", "Coffee and pastry at local cafe."));
        expenses.add(new Expense(nextId++, "Lunch", 12.0, "2025-10-15", "Takeout sandwich and a drink."));
        expenses.add(new Expense(nextId++, "Dinner", 8.5, "2025-10-14", "Home cooked meal ingredients."));
        expenses.add(new Expense(nextId++, "Snacks", 3.5, "2025-10-14", "Bag of chips and soda."));
        expenses.add(new Expense(nextId++, "Boba Tea", 4.5, "2025-10-13", "Brown sugar milk tea."));
        expenses.add(new Expense(nextId++, "Pizza", 11.0, "2025-10-13", "Shared medium pizza with friends."));
        expenses.add(new Expense(nextId++, "Fried Rice", 6.25, "2025-10-12", "Street food fried rice with egg."));
        expenses.add(new Expense(nextId++, "Burger & Fries", 9.5, "2025-10-11", "Fast food lunch combo."));
        expenses.add(new Expense(nextId++, "Ice Cream", 2.75, "2025-10-11", "Vanilla cone dessert."));
        expenses.add(new Expense(nextId++, "Noodles", 7.0, "2025-10-10", "Beef noodle soup at local restaurant."));
        expenses.add(new Expense(nextId++, "Sushi Set", 14.0, "2025-10-10", "Salmon sushi platter."));
        expenses.add(new Expense(nextId++, "Salad", 5.5, "2025-10-09", "Green salad with chicken topping."));
        expenses.add(new Expense(nextId++, "Steak Dinner", 18.0, "2025-10-08", "Restaurant dinner with drink."));
        expenses.add(new Expense(nextId++, "Fruit Smoothie", 3.8, "2025-10-08", "Banana mango smoothie."));
        expenses.add(new Expense(nextId++, "Pasta", 10.5, "2025-10-07", "Creamy Alfredo pasta."));
        expenses.add(new Expense(nextId++, "Sandwich", 4.0, "2025-10-07", "Ham and cheese sandwich."));
        expenses.add(new Expense(nextId++, "Coffee", 2.5, "2025-10-06", "Morning black coffee."));
        expenses.add(new Expense(nextId++, "Curry Rice", 7.5, "2025-10-05", "Japanese curry with chicken."));
        expenses.add(new Expense(nextId++, "Hot Pot", 20.0, "2025-10-04", "Dinner with friends at hot pot place."));
        expenses.add(new Expense(nextId++, "Fried Chicken", 9.0, "2025-10-03", "Korean-style fried chicken."));
        expenses.add(new Expense(nextId++, "Dim Sum", 13.5, "2025-10-02", "Brunch with dumplings and tea."));
        expenses.add(new Expense(nextId++, "Ramen", 8.0, "2025-10-01", "Tonkotsu ramen lunch."));
        expenses.add(new Expense(nextId++, "Breakfast Set", 6.0, "2025-09-30", "Egg, toast, and coffee combo."));
        expenses.add(new Expense(nextId++, "Grilled Fish", 12.5, "2025-09-29", "Dinner with grilled mackerel."));
        expenses.add(new Expense(nextId++, "Pancakes", 7.0, "2025-09-28", "Brunch pancakes with syrup."));
        expenses.add(new Expense(nextId++, "BBQ Buffet", 22.0, "2025-09-27", "All-you-can-eat buffet."));
        expenses.add(new Expense(nextId++, "Street Tacos", 5.5, "2025-09-27", "3 tacos with lime and sauce."));
        expenses.add(new Expense(nextId++, "Soup", 4.5, "2025-09-26", "Vegetable soup for lunch."));
        expenses.add(new Expense(nextId++, "Donuts", 3.0, "2025-09-25", "2 glazed donuts for breakfast."));
        expenses.add(new Expense(nextId++, "Seafood Pasta", 14.5, "2025-09-24", "Shrimp and clam pasta dinner."));
        expenses.add(new Expense(nextId++, "Coconut Water", 2.0, "2025-09-24", "Refreshing drink on hot day."));
    }

    // 1. SAVE/ADD METHOD (Used by AddExpenseActivity)
    public void saveExpense(Expense expense) {
        expense.setId(nextId++); // Assign a unique ID
        expenses.add(0, expense); // Add to the start so it appears first on the list
    }

    // 2. RETRIEVE ALL (Used by HomeFragment)
    public List<Expense> getAllExpenses() {
        return expenses;
    }

    // 3. RETRIEVE BY ID (Used by ExpenseDetailActivity)
    public Expense getExpenseById(long id) {
        for (Expense expense : expenses) {
            if (expense.getId() == id) {
                return expense;
            }
        }
        return null;
    }
}
