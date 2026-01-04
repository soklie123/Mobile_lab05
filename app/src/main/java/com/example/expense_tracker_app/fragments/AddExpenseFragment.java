package com.example.expense_tracker_app.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.api.ExpenseApi;
import com.example.expense_tracker_app.databinding.FragmentAddExpenseBinding;
import com.example.expense_tracker_app.models.Expense;
import com.example.expense_tracker_app.utils.CategoryViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {

    private FragmentAddExpenseBinding binding;
    private CategoryViewModel categoryViewModel;

    // Image selection variables
    private Uri selectedImageUri;
    private String currentPhotoPath;

    // Activity result launchers
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

    private String selectedDate;
    private boolean isEditMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityResultLaunchers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        // Initialize ViewModel
        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        // Setup toolbar
        binding.addToolbar.setTitle("Add Expense");
        binding.addToolbar.setNavigationOnClickListener(v -> navController.popBackStack());

        // Setup category spinner
        setupCategorySpinner();

        // Setup date picker
        setupDatePicker();

        // Setup image selection
        setupImageSelection();

        // Setup save button
        binding.btnSaveExpense.setOnClickListener(v -> saveExpense(navController));

        // Check if edit mode
        checkEditMode();
    }

    private void setupActivityResultLaunchers() {
        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK
                            && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        displayImagePreview(selectedImageUri);
                    }
                }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK) {
                        File photoFile = new File(currentPhotoPath);
                        selectedImageUri = Uri.fromFile(photoFile);
                        displayImagePreview(selectedImageUri);
                    }
                }
        );

        // Camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(requireContext(),
                                "Camera permission is required to take photos",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Storage permission launcher (for Android 13+)
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(requireContext(),
                                "Storage permission is required to access photos",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupCategorySpinner() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                String[] categoryNames = new String[categories.size()];
                for (int i = 0; i < categories.size(); i++) {
                    categoryNames[i] = categories.get(i).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        categoryNames
                );
                binding.spinnerCategory.setAdapter(adapter);
            }
        });

        // Add category button
        binding.btnAddCategory.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_addExpenseFragment_to_addCategoryFragment);
        });
    }

    private void setupDatePicker() {
        binding.layoutDate.setEndIconOnClickListener(v -> showDatePicker());
        binding.editDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDate = sdf.format(new Date(selection));
            binding.editDate.setText(selectedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void setupImageSelection() {
        // Pick from gallery
        binding.btnPickFromGallery.setOnClickListener(v -> checkStoragePermissionAndOpenGallery());

        // Take photo with camera
        binding.btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndOpen());

        // Remove image
        binding.btnRemoveImage.setOnClickListener(v -> removeImage());
    }

    private void checkStoragePermissionAndOpenGallery() {
        // For Android 13+ (API 33+), we need READ_MEDIA_IMAGES
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // For older Android versions
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(requireContext(), "Error creating image file",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        } else {
            Toast.makeText(requireContext(), "No camera app found",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void displayImagePreview(Uri imageUri) {
        binding.cardImagePreview.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(imageUri)
                .centerCrop()
                .into(binding.imagePreview);
    }

    private void removeImage() {
        selectedImageUri = null;
        currentPhotoPath = null;
        binding.cardImagePreview.setVisibility(View.GONE);
        binding.imagePreview.setImageDrawable(null);
    }

    private void checkEditMode() {
        Bundle args = getArguments();
        if (args != null) {
            isEditMode = args.getBoolean("isEditMode", false);
            if (isEditMode) {
                binding.addToolbar.setTitle("Edit Expense");
                // Load existing data...
                String receiptUrl = args.getString("expense_receipt_image_url");
                if (!TextUtils.isEmpty(receiptUrl)) {
                    selectedImageUri = Uri.parse(receiptUrl);
                    displayImagePreview(selectedImageUri);
                }
            }
        }
    }

    private void saveExpense(NavController navController) {
        // Get input values
        String foodName = binding.editFoodName.getText().toString().trim();
        String amountStr = binding.editAmount.getText().toString().trim();
        String category = binding.spinnerCategory.getText().toString().trim();
        String description = binding.editDescription.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(foodName)) {
            binding.editFoodName.setError("Required");
            binding.editFoodName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            binding.editAmount.setError("Required");
            binding.editAmount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            binding.spinnerCategory.setError("Required");
            binding.spinnerCategory.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(selectedDate)) {
            binding.editDate.setError("Required");
            binding.editDate.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            binding.editAmount.setError("Invalid amount");
            binding.editAmount.requestFocus();
            return;
        }

        // Create expense object
        Expense expense = new Expense(
                amount,
                "USD",
                category,
                foodName,
                description,
                selectedDate + " 00:00:00", // Add time format
                "user_id" // Replace with actual user ID from SharedPreferences
        );

        // Set receipt image URL if available
        if (selectedImageUri != null) {
            expense.setReceiptImageUrl(selectedImageUri.toString());
        }

        // Save to API
        saveExpenseToApi(expense, navController);
    }

    private void saveExpenseToApi(Expense expense, NavController navController) {
        // Show loading
        binding.btnSaveExpense.setEnabled(false);
        binding.btnSaveExpense.setText("Saving...");

        ExpenseApi expenseApi = new ExpenseApi();
        expenseApi.createExpense(expense, new ExpenseApi.ApiCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;

                // Notify HomeFragment to refresh
                Bundle result = new Bundle();
                result.putBoolean("refresh", true);
                getParentFragmentManager().setFragmentResult("requestKey", result);

                Toast.makeText(requireContext(), "Expense saved successfully!",
                        Toast.LENGTH_SHORT).show();

                navController.popBackStack();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;

                Toast.makeText(requireContext(), "Failed to save: " + errorMessage,
                        Toast.LENGTH_LONG).show();

                // Re-enable button
                binding.btnSaveExpense.setEnabled(true);
                binding.btnSaveExpense.setText("Save Expense");
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}