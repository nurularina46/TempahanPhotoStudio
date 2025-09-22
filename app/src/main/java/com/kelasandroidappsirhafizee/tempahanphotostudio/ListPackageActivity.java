package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListPackageActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPackages;
    private Button btnAddPackage;
    private TextView tvCategoryTitle;
    private String category, role, username;
    private ConnectionClass connectionClass;
    private ArrayList<PackageModel> packageList;
    private PackageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_package);

        // Get data from Intent
        category = getIntent().getStringExtra("CATEGORY");
        role = getIntent().getStringExtra("ROLE");
        username = getIntent().getStringExtra("USERNAME");

        recyclerViewPackages = findViewById(R.id.recyclerViewPackages);
        btnAddPackage = findViewById(R.id.btnAddPackage);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);

        connectionClass = new ConnectionClass();
        tvCategoryTitle.setText(category + " Packages");

        // Hanya admin boleh tambah package
        if ("Admin".equals(role)) {
            btnAddPackage.setVisibility(View.VISIBLE);
            btnAddPackage.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddEditPackageActivity.class);
                intent.putExtra("CATEGORY", category);
                intent.putExtra("ROLE", role);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            });
        } else {
            btnAddPackage.setVisibility(View.GONE);
        }

        recyclerViewPackages.setLayoutManager(new LinearLayoutManager(this));

        packageList = new ArrayList<>();
        adapter = new PackageAdapter(packageList, role,selectedPackage -> {
            if ("Admin".equals(role)) {
                Intent intent = new Intent(this, AddEditPackageActivity.class);
                intent.putExtra("PACKAGE_ID", selectedPackage.getId());
                intent.putExtra("CATEGORY", category);
                intent.putExtra("ROLE", role);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ListSubPackageActivity.class);
                intent.putExtra("PACKAGE_ID", selectedPackage.getId());
                intent.putExtra("PACKAGE_NAME", selectedPackage.getPackageName());
                intent.putExtra("CATEGORY", category);
                intent.putExtra("ROLE", role);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });
        recyclerViewPackages.setAdapter(adapter);

        loadPackages();
    }

    private void loadPackages() {
        new Thread(() -> {
            ArrayList<PackageModel> result = connectionClass.getPackagesByCategory(category);

            runOnUiThread(() -> {
                if (result == null) {
                    Toast.makeText(this, "Error loading packages", Toast.LENGTH_SHORT).show();
                    return;
                }

                packageList.clear();
                packageList.addAll(result);
                adapter.notifyDataSetChanged();

                if (packageList.isEmpty()) {
                    Toast.makeText(this, "No packages found for " + category, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPackages();
    }
}
