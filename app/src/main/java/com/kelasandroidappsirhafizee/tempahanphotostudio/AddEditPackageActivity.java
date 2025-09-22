package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditPackageActivity extends AppCompatActivity {
    private EditText etPackageName, etEvent, etDuration, etDescription;
    private Button btnSave, btnDelete;
    private TextView tvTitle;
    private ConnectionClass connectionClass;
    private String category, role, username;
    private int packageId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_package);

        // Get intent data
        category = getIntent().getStringExtra("CATEGORY");
        role = getIntent().getStringExtra("ROLE");
        username = getIntent().getStringExtra("USERNAME");
        packageId = getIntent().getIntExtra("PACKAGE_ID", -1);

        // Initialize views
        etPackageName = findViewById(R.id.etPackageName);
        etEvent = findViewById(R.id.etEvent);
        etDuration = findViewById(R.id.etDuration);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        tvTitle = findViewById(R.id.tvTitle);

        connectionClass = new ConnectionClass();

        // Check if editing
        if (packageId != -1) {
            isEditMode = true;
            tvTitle.setText("Edit Package");
            btnDelete.setVisibility(Button.VISIBLE);
            loadPackageData(packageId);
        } else {
            tvTitle.setText("Add New Package");
            btnDelete.setVisibility(Button.GONE);
        }

        btnSave.setOnClickListener(v -> savePackage());
        btnDelete.setOnClickListener(v -> deletePackage());
    }

    private void loadPackageData(int packageId) {
        new Thread(() -> {
            PackageModel pkg = connectionClass.getPackageById(packageId);
            runOnUiThread(() -> {
                if (pkg != null) {
                    etPackageName.setText(pkg.getPackageName());
                    etEvent.setText(pkg.getEvent());
                    etDuration.setText(pkg.getDuration());
                    etDescription.setText(pkg.getDescription());
                } else {
                    Toast.makeText(this, "Failed to load package data", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }).start();
    }

    private void savePackage() {
        String packageName = etPackageName.getText().toString().trim();
        String event = etEvent.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (packageName.isEmpty() || event.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        PackageModel pkg = new PackageModel();
        pkg.setId(packageId);
        pkg.setPackageName(packageName);
        pkg.setEvent(event);
        pkg.setDuration(duration);
        pkg.setCategory(category);
        pkg.setDescription(description);
        pkg.setImageUrl("");

        new Thread(() -> {
            String result;
            if (isEditMode) {
                result = connectionClass.updatePackage(pkg);
            } else {
                result = connectionClass.addPackage(pkg);
            }

            String finalResult = result;
            runOnUiThread(() -> {
                if ("success".equals(finalResult)) {
                    Toast.makeText(this, isEditMode ? "Package updated" : "Package added", Toast.LENGTH_SHORT).show();
                    goBackToList();
                } else {
                    Toast.makeText(this, "Failed: " + finalResult, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void deletePackage() {
        if (packageId == -1) return;

        new Thread(() -> {
            String result = connectionClass.deletePackage(packageId);
            runOnUiThread(() -> {
                if ("success".equals(result)) {
                    Toast.makeText(this, "Package deleted", Toast.LENGTH_SHORT).show();
                    goBackToList();
                } else {
                    Toast.makeText(this, "Failed to delete: " + result, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void goBackToList() {
        Intent intent = new Intent(this, ListPackageActivity.class);
        intent.putExtra("CATEGORY", category);
        intent.putExtra("ROLE", role);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }
}
