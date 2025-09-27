package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddVideographySubPackageActivity extends AppCompatActivity {
    
    private ImageButton btnBack;
    private TextView tvPackageName, tvPackageEvent, tvPackageDuration, tvStatus;
    private Spinner spinnerPackageClass;
    private EditText edtPrice, edtDetails, edtDescription;
    private CheckBox chkIsActive;
    private Button btnTestConnection, btnSave;
    
    private ConnectionClass connectionClass;
    private int packageId;
    private String packageName, packageEvent, packageDuration, category, role, username;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_videography_subpackage);
        
        initViews();
        setupSpinner();
        getIntentData();
        setupClickListeners();
        displayPackageInfo();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvPackageName = findViewById(R.id.tvPackageName);
        tvPackageEvent = findViewById(R.id.tvPackageEvent);
        tvPackageDuration = findViewById(R.id.tvPackageDuration);
        tvStatus = findViewById(R.id.tvStatus);
        spinnerPackageClass = findViewById(R.id.spinnerPackageClass);
        edtPrice = findViewById(R.id.edtPrice);
        edtDetails = findViewById(R.id.edtDetails);
        edtDescription = findViewById(R.id.edtDescription);
        chkIsActive = findViewById(R.id.chkIsActive);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        btnSave = findViewById(R.id.btnSave);
        
        connectionClass = new ConnectionClass();
    }
    
    private void setupSpinner() {
        String[] packageClasses = {"Regular", "Advance", "Premium"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, packageClasses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPackageClass.setAdapter(adapter);
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        packageId = intent.getIntExtra("PACKAGE_ID", -1);
        packageName = intent.getStringExtra("PACKAGE_NAME");
        packageEvent = intent.getStringExtra("PACKAGE_EVENT");
        packageDuration = intent.getStringExtra("PACKAGE_DURATION");
        category = intent.getStringExtra("CATEGORY");
        role = intent.getStringExtra("ROLE");
        username = intent.getStringExtra("USERNAME");
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnTestConnection.setOnClickListener(v -> testDatabaseConnection());
        
        btnSave.setOnClickListener(v -> saveSubPackage());
    }
    
    private void displayPackageInfo() {
        if (packageName != null) {
            tvPackageName.setText("Package: " + packageName);
        }
        if (packageEvent != null) {
            tvPackageEvent.setText("Event: " + packageEvent);
        }
        if (packageDuration != null) {
            tvPackageDuration.setText("Duration: " + packageDuration);
        }
    }
    
    private void testDatabaseConnection() {
        btnTestConnection.setEnabled(false);
        btnTestConnection.setText("Testing...");
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("Testing database connection...");
        
        new Thread(() -> {
            String result = connectionClass.testSubPackageTable();
            
            runOnUiThread(() -> {
                btnTestConnection.setEnabled(true);
                btnTestConnection.setText("Test Connection");
                tvStatus.setText(result);
                
                if (result.contains("SUCCESS")) {
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            });
        }).start();
    }
    
    private void saveSubPackage() {
        // Validate input
        String packageClass = spinnerPackageClass.getSelectedItem().toString();
        String priceStr = edtPrice.getText().toString().trim();
        String details = edtDetails.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        boolean isActive = chkIsActive.isChecked();
        
        if (priceStr.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please fill in price and details", Toast.LENGTH_SHORT).show();
            return;
        }
        
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (packageId == -1) {
            Toast.makeText(this, "Invalid package ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create SubPackageModel
        SubPackageModel subPackage = new SubPackageModel(
                0, // ID will be auto-generated
                packageId,
                packageClass,
                price,
                details,
                description,
                isActive
        );
        
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");
        tvStatus.setVisibility(View.VISIBLE);
        tvStatus.setText("Saving sub-package...");
        
        new Thread(() -> {
            boolean success = connectionClass.insertSubPackage(subPackage);
            
            runOnUiThread(() -> {
                btnSave.setEnabled(true);
                btnSave.setText("Save Sub-Package");
                
                if (success) {
                    tvStatus.setText("✅ Sub-package saved successfully!");
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    
                    // Clear form
                    edtPrice.setText("");
                    edtDetails.setText("");
                    edtDescription.setText("");
                    spinnerPackageClass.setSelection(0);
                    chkIsActive.setChecked(true);
                    
                    Toast.makeText(this, "Sub-package added successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Go back to list after 2 seconds
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            runOnUiThread(() -> finish());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    
                } else {
                    tvStatus.setText("❌ Failed to save sub-package. Check database connection.");
                    tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    Toast.makeText(this, "Failed to save sub-package", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
