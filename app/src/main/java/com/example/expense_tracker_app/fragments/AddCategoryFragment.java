package com.example.expense_tracker_app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.models.Category;
import com.example.expense_tracker_app.utils.CategoryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddCategoryFragment extends Fragment {

    private CategoryViewModel categoryViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        TextInputEditText editCategory = view.findViewById(R.id.edit_new_category);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_category);

        categoryViewModel = new ViewModelProvider(requireActivity())
                .get(CategoryViewModel.class);

        btnSave.setOnClickListener(v -> {
            String name = editCategory.getText().toString().trim();

            if (name.isEmpty()) {
                editCategory.setError("Required");
                return;
            }

            categoryViewModel.insert(new Category(name));

            Toast.makeText(getContext(), "Category added", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
        });

        return view;
    }
}
