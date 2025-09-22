package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PackageCategoryActivity extends AppCompatActivity {
    private Button btnPhotography, btnVideography;
    private String role;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_category);

        role = getIntent().getStringExtra("ROLE");
        String username = getIntent().getStringExtra("USERNAME");

        btnPhotography = findViewById(R.id.btnPhotography);
        btnVideography = findViewById(R.id.btnVideography);
        tvWelcome = findViewById(R.id.tvWelcome);

        // Set welcome message based on role
        if (role != null && role.equals("Admin")) {
            tvWelcome.setText("Welcome Admin, " + username);
        } else {
            tvWelcome.setText("Welcome " + username);
        }

        btnPhotography.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListPackageActivity.class);
            intent.putExtra("CATEGORY", "Photography");
            intent.putExtra("ROLE", role);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        btnVideography.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListPackageActivity.class);
            intent.putExtra("CATEGORY", "Videography");
            intent.putExtra("ROLE", role);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
    }
}