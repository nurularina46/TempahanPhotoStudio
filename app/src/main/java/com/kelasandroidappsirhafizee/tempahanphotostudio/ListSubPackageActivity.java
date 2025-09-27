package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListSubPackageActivity extends AppCompatActivity {
    private ListView listViewSubPackages;
    private Button btnAddSubPackage;
    private TextView tvPackageTitle;
    private String packageName, category, role, username;
    private int packageId;
    private ConnectionClass connectionClass;
    private ArrayList<SubPackageModel> subPackageList; // guna ArrayList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sub_package);

        // Get intent data
        packageId = getIntent().getIntExtra("PACKAGE_ID", -1);
        packageName = getIntent().getStringExtra("PACKAGE_NAME");
        category = getIntent().getStringExtra("CATEGORY");
        role = getIntent().getStringExtra("ROLE");
        username = getIntent().getStringExtra("USERNAME");

        // Initialize views
        listViewSubPackages = findViewById(R.id.listViewSubPackages);
        btnAddSubPackage = findViewById(R.id.btnAddSubPackage);
        tvPackageTitle = findViewById(R.id.tvPackageTitle);

        connectionClass = new ConnectionClass();

        // Set package title
        tvPackageTitle.setText(packageName + " - Sub Packages");

        // Show/hide add button based on role
        if ("Admin".equals(role)) {
            btnAddSubPackage.setVisibility(View.VISIBLE);
            btnAddSubPackage.setOnClickListener(v -> {
                // Check if this is videography package
                if ("Videography".equals(category)) {
                    // Navigate to AddVideographySubPackageActivity
                    Intent intent = new Intent(this, AddVideographySubPackageActivity.class);
                    intent.putExtra("PACKAGE_ID", packageId);
                    intent.putExtra("PACKAGE_NAME", packageName);
                    intent.putExtra("PACKAGE_EVENT", getIntent().getStringExtra("PACKAGE_EVENT"));
                    intent.putExtra("PACKAGE_DURATION", getIntent().getStringExtra("PACKAGE_DURATION"));
                    intent.putExtra("CATEGORY", category);
                    intent.putExtra("ROLE", role);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                } else {
                    // Navigate to AddEditSubPackageActivity for other categories
                    Intent intent = new Intent(this, AddEditSubPackageActivity.class);
                    intent.putExtra("PACKAGE_ID", packageId);
                    intent.putExtra("PACKAGE_NAME", packageName);
                    intent.putExtra("CATEGORY", category);
                    intent.putExtra("ROLE", role);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                }
            });
        } else {
            btnAddSubPackage.setVisibility(View.GONE);
        }

        // Load sub-packages
        loadSubPackages();

        // List item click
        listViewSubPackages.setOnItemClickListener((parent, view, position, id) -> {
            SubPackageModel selectedSubPackage = subPackageList.get(position);

            if ("Admin".equals(role)) {
                // Admin: edit sub-package
                Intent intent = new Intent(ListSubPackageActivity.this, AddEditSubPackageActivity.class);
                intent.putExtra("SUB_PACKAGE_ID", selectedSubPackage.getId());
                intent.putExtra("PACKAGE_ID", packageId);
                intent.putExtra("PACKAGE_NAME", packageName);
                intent.putExtra("CATEGORY", category);
                intent.putExtra("ROLE", role);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            } else {
                // User: go to booking
                Intent intent = new Intent(ListSubPackageActivity.this, BookingDetailsActivity.class);
                intent.putExtra("PACKAGE_ID", packageId);
                intent.putExtra("PACKAGE_NAME", packageName);
                intent.putExtra("SUB_PACKAGE_ID", selectedSubPackage.getId());
                intent.putExtra("SUB_PACKAGE_CLASS", selectedSubPackage.getPackageClass());
                intent.putExtra("SUB_PACKAGE_PRICE", selectedSubPackage.getPrice());
                intent.putExtra("SUB_PACKAGE_DETAILS", selectedSubPackage.getDetails());
                intent.putExtra("CATEGORY", category);
                intent.putExtra("ROLE", role);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });
    }

    private void loadSubPackages() {
        new Thread(() -> {
            try {
                ArrayList<SubPackageModel> result = connectionClass.getSubPackagesByPackageId(packageId);

                runOnUiThread(() -> {
                    if (result == null) {
                        Toast.makeText(this, "Database connection failed", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                        return;
                    }
                    
                    if (result.isEmpty()) {
                        Toast.makeText(this, "No sub-packages found. Admin can add sub-packages.", Toast.LENGTH_LONG).show();
                        showEmptyState();
                        return;
                    }

                    subPackageList = result;

                    // Buat senarai untuk dipaparkan
                    ArrayList<String> subPackageNames = new ArrayList<>();
                    for (SubPackageModel subPackage : subPackageList) {
                        subPackageNames.add(
                                subPackage.getPackageClass() + "\n" +
                                        "Details: " + subPackage.getDetails() + "\n" +
                                        "Price: RM " + String.format("%.2f", subPackage.getPrice())
                        );
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_list_item_1,
                            subPackageNames
                    );
                    listViewSubPackages.setAdapter(adapter);
                    
                    Toast.makeText(this, "Loaded " + subPackageList.size() + " sub-packages", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading sub-packages: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    showEmptyState();
                });
            }
        }).start();
    }

    private void showEmptyState() {
        // You can add empty state UI here if needed
        Toast.makeText(this, "No sub-packages available. Admin can add sub-packages.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubPackages(); // Refresh data bila balik dari Add/Edit
    }
}
