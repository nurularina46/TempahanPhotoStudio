package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddEditSubPackageActivity extends AppCompatActivity {
    private EditText edtPrice, edtDetails, edtDescription;
    private Spinner spinnerClass;
    private Button btnSave, btnDelete;
    private ConnectionClass connectionClass;

    private int subPackageId = -1; // -1 = add new
    private int packageId;
    private String packageName, category, role, username;
    
    private String[] packageClasses = {"Regular", "Advance", "Premium"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_sub_package);

        spinnerClass = findViewById(R.id.spinnerSubPackageClass);
        edtPrice = findViewById(R.id.edtSubPackagePrice);
        edtDetails = findViewById(R.id.edtSubPackageDetails);
        edtDescription = findViewById(R.id.edtSubPackageDescription);
        btnSave = findViewById(R.id.btnSaveSubPackage);
        btnDelete = findViewById(R.id.btnDeleteSubPackage);

        connectionClass = new ConnectionClass();

        // Get intent extras
        subPackageId = getIntent().getIntExtra("SUB_PACKAGE_ID", -1);
        packageId = getIntent().getIntExtra("PACKAGE_ID", -1);
        packageName = getIntent().getStringExtra("PACKAGE_NAME");
        category = getIntent().getStringExtra("CATEGORY");
        role = getIntent().getStringExtra("ROLE");
        username = getIntent().getStringExtra("USERNAME");

        // Setup spinner
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, packageClasses);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);

        // Kalau edit, load data dulu
        if (subPackageId != -1) {
            loadSubPackage(subPackageId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveSubPackage());
        btnDelete.setOnClickListener(v -> deleteSubPackage());
        
        // Add test button for debugging
        Button btnTest = findViewById(R.id.btnTestConnection);
        if (btnTest != null) {
            btnTest.setOnClickListener(v -> testDatabaseConnection());
        }
    }

    private void loadSubPackage(int subPackageId) {
        new Thread(() -> {
            SubPackageModel subPackage = connectionClass.getSubPackageById(subPackageId);

            runOnUiThread(() -> {
                if (subPackage != null) {
                    // Set spinner selection
                    for (int i = 0; i < packageClasses.length; i++) {
                        if (packageClasses[i].equals(subPackage.getPackageClass())) {
                            spinnerClass.setSelection(i);
                            break;
                        }
                    }
                    edtPrice.setText(String.valueOf(subPackage.getPrice()));
                    edtDetails.setText(subPackage.getDetails());
                    edtDescription.setText(subPackage.getDescription());
                } else {
                    Toast.makeText(this, "Failed to load sub-package", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void saveSubPackage() {
        String subClass = spinnerClass.getSelectedItem().toString();
        String subPriceStr = edtPrice.getText().toString().trim();
        String subDetails = edtDetails.getText().toString().trim();
        String subDescription = edtDescription.getText().toString().trim();

        if (subPriceStr.isEmpty() || subDetails.isEmpty()) {
            Toast.makeText(this, "Please fill in price and details", Toast.LENGTH_SHORT).show();
            return;
        }

        double subPrice;
        try {
            subPrice = Double.parseDouble(subPriceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate package ID first
        new Thread(() -> {
            ArrayList<Integer> validPackageIds = connectionClass.getVideographyPackageIds();
            
            runOnUiThread(() -> {
                if (validPackageIds.isEmpty()) {
                    Toast.makeText(this, "No videography packages found! Please add packages first.", Toast.LENGTH_LONG).show();
                    return;
                }
                
                if (!validPackageIds.contains(packageId)) {
                    Toast.makeText(this, "Invalid package ID: " + packageId + ". Valid IDs: " + validPackageIds.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                
                // Proceed with save
                saveSubPackageToDatabase(subClass, subPrice, subDetails, subDescription);
            });
        }).start();
    }

    private void saveSubPackageToDatabase(String subClass, double subPrice, String subDetails, String subDescription) {
        SubPackageModel newSubPackage = new SubPackageModel(
                subPackageId == -1 ? 0 : subPackageId, // 0 kalau add new
                packageId,
                subClass,
                subPrice,
                subDetails,
                subDescription,
                true // isActive = true
        );

        new Thread(() -> {
            boolean success;
            if (subPackageId == -1) {
                // Insert
                success = connectionClass.insertSubPackage(newSubPackage);
            } else {
                // Update
                success = connectionClass.updateSubPackage(newSubPackage);
            }

            runOnUiThread(() -> {
                if (success) {
                    String action = subPackageId == -1 ? "added" : "updated";
                    Toast.makeText(this, "Sub-package " + action + " successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save sub-package", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void deleteSubPackage() {
        if (subPackageId == -1) return;

        new Thread(() -> {
            boolean success = connectionClass.deleteSubPackage(subPackageId);
            
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "Sub-package deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to delete sub-package", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void testDatabaseConnection() {
        new Thread(() -> {
            try {
                // Test basic connection
                String result = connectionClass.testSubPackageTable();
                
                // Get available package IDs
                ArrayList<Integer> packageIds = connectionClass.getVideographyPackageIds();
                
                runOnUiThread(() -> {
                    String message = result + "\n\nAvailable Package IDs: " + packageIds.toString();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    System.out.println("Database test result: " + result);
                    System.out.println("Available package IDs: " + packageIds.toString());
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Test failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println("Test error: " + e.getMessage());
                });
            }
        }).start();
    }
}
