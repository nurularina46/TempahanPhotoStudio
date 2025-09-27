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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class VideographyPackageActivity extends AppCompatActivity {
    private ListView listViewPackages;
    private Button btnAddPackage;
    private TextView tvCategoryTitle;
    private String category, role, username;
    private ConnectionClass connectionClass;
    private List<PackageModel> packageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videography_package);

        // Get intent data
        category = getIntent().getStringExtra("CATEGORY");
        role = getIntent().getStringExtra("ROLE");
        username = getIntent().getStringExtra("USERNAME");

        // Initialize views
        listViewPackages = findViewById(R.id.listViewVideographyPackages);
        btnAddPackage = findViewById(R.id.btnAddVideographyPackage);
        tvCategoryTitle = findViewById(R.id.tvVideographyTitle);

        // Initialize connection class
        connectionClass = new ConnectionClass();

        // Set category title
        tvCategoryTitle.setText("Videography Packages");

        // Show/hide add button based on role
        if (role != null && role.equals("Admin")) {
            btnAddPackage.setVisibility(View.VISIBLE);
            btnAddPackage.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddEditPackageActivity.class);
                intent.putExtra("CATEGORY", "Videography");
                intent.putExtra("ROLE", role);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            });
        } else {
            btnAddPackage.setVisibility(View.GONE);
        }

        // Load packages
        loadVideographyPackages();

        // Set list item click listener
        listViewPackages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackageModel selectedPackage = packageList.get(position);

                Intent intent;
                if (role != null && role.equals("Admin")) {
                    // Admin can edit/delete packages
                    intent = new Intent(VideographyPackageActivity.this, AddEditPackageActivity.class);
                    intent.putExtra("PACKAGE_ID", selectedPackage.getId());
                } else {
                    // User can view sub-packages
                    intent = new Intent(VideographyPackageActivity.this, ListSubPackageActivity.class);
                    intent.putExtra("PACKAGE_ID", selectedPackage.getId());
                    intent.putExtra("PACKAGE_NAME", selectedPackage.getPackageName());
                    intent.putExtra("PACKAGE_EVENT", selectedPackage.getEvent());
                    intent.putExtra("PACKAGE_DURATION", selectedPackage.getDuration());
                }
                intent.putExtra("CATEGORY", "Videography");
                intent.putExtra("ROLE", role);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });
    }

    private void loadVideographyPackages() {
        new Thread(() -> {
            try {
                packageList = connectionClass.getPackagesByCategory("Videography");
                
                runOnUiThread(() -> {
                    if (packageList == null) {
                        Toast.makeText(this, "Database connection failed", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                        return;
                    }
                    
                    if (packageList.isEmpty()) {
                        Toast.makeText(this, "No videography packages found. Admin can add packages.", Toast.LENGTH_LONG).show();
                        showEmptyState();
                        return;
                    }

                    // Create adapter for ListView
                    ArrayAdapter<String> adapter = getStringArrayAdapter();
                    listViewPackages.setAdapter(adapter);
                    
                    Toast.makeText(this, "Loaded " + packageList.size() + " videography packages", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading packages: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    showEmptyState();
                });
            }
        }).start();
    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter() {
        List<String> packageNames = new ArrayList<>();
        for (PackageModel packageModel : packageList) {
            packageNames.add(packageModel.getPackageName() + "\n" +
                           "Event: " + packageModel.getEvent() + " | " +
                           "Duration: " + packageModel.getDuration());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, packageNames);
        return adapter;
    }

    private void showEmptyState() {
        // You can add empty state UI here if needed
        Toast.makeText(this, "No packages available. Admin can add packages.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVideographyPackages(); // Refresh the list when returning from other activities
    }
}
