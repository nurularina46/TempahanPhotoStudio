package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditSubPackageActivity extends AppCompatActivity {
    private EditText edtClass, edtPrice, edtDetails;
    private Button btnSave;
    private ConnectionClass connectionClass;

    private int subPackageId = -1; // -1 = add new
    private int packageId;
    private String packageName, category, role, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_sub_package);

        edtClass = findViewById(R.id.edtSubPackageClass);
        edtPrice = findViewById(R.id.edtSubPackagePrice);
        edtDetails = findViewById(R.id.edtSubPackageDetails);
        btnSave = findViewById(R.id.btnSaveSubPackage);

        connectionClass = new ConnectionClass();

        // Get intent extras
        subPackageId = getIntent().getIntExtra("SUB_PACKAGE_ID", -1);
        packageId = getIntent().getIntExtra("PACKAGE_ID", -1);
        packageName = getIntent().getStringExtra("PACKAGE_NAME");
        category = getIntent().getStringExtra("CATEGORY");
        role = getIntent().getStringExtra("ROLE");
        username = getIntent().getStringExtra("USERNAME");

        // Kalau edit, load data dulu
        if (subPackageId != -1) {
            loadSubPackage(subPackageId);
        }

        btnSave.setOnClickListener(v -> saveSubPackage());
    }

    private void loadSubPackage(int subPackageId) {
        new Thread(() -> {
            SubPackageModel subPackage = connectionClass.getSubPackageById(subPackageId);

            runOnUiThread(() -> {
                if (subPackage != null) {
                    edtClass.setText(subPackage.getPackageClass());
                    edtPrice.setText(String.valueOf(subPackage.getPrice()));
                    edtDetails.setText(subPackage.getDetails());
                } else {
                    Toast.makeText(this, "Failed to load sub-package", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void saveSubPackage() {
        String subClass = edtClass.getText().toString().trim();
        String subPriceStr = edtPrice.getText().toString().trim();
        String subDetails = edtDetails.getText().toString().trim();

        if (subClass.isEmpty() || subPriceStr.isEmpty() || subDetails.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double subPrice;
        try {
            subPrice = Double.parseDouble(subPriceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        SubPackageModel newSubPackage = new SubPackageModel(
                subPackageId == -1 ? 0 : subPackageId, // 0 kalau add new
                packageId,
                subClass,
                subPrice,
                subDetails
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
                    Toast.makeText(this, "Sub-package saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save sub-package", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
