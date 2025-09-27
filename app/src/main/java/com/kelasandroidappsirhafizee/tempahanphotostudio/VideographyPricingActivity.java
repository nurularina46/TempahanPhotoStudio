package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideographyPricingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videography_pricing);

        // Setup back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Add click listeners for each package
        setupPackageClickListeners();
    }

    private void setupPackageClickListeners() {
        // PV 5 Package
        findViewById(R.id.pv5_card).setOnClickListener(v -> {
            showPackageDetails("PV 5", "3 Event 1 Video", "RM 2800");
        });

        // PV 4 Package
        findViewById(R.id.pv4_card).setOnClickListener(v -> {
            showPackageDetails("PV 4", "3 Event 1 Video", "RM 2100");
        });

        // PV 3 Package
        findViewById(R.id.pv3_card).setOnClickListener(v -> {
            showPackageDetails("PV 3", "2 Event 1 Video", "RM 2100");
        });

        // PV 2 Package
        findViewById(R.id.pv2_card).setOnClickListener(v -> {
            showPackageDetails("PV 2", "2 Event 1 Video", "RM 2000");
        });

        // PV 1 Package
        findViewById(R.id.pv1_card).setOnClickListener(v -> {
            showPackageDetails("PV 1", "1 Event 1 Video", "RM 1500");
        });
    }

    private void showPackageDetails(String packageName, String service, String price) {
        // Navigate to single sub-package page
        Intent intent = SubPackageSingleActivity.createIntent(this, 
            packageName, 
            service, 
            getEventsForPackage(packageName),
            getEventDetailsForPackage(packageName),
            "6-7 Hours Per Day",
            getDeliverablesForPackage(packageName),
            "• Drone 1 Per Day: RM500\n• +1 Videographer Per Day: RM500\n• Raw Footage + 64GB: RM150\n• Persiapan/Makeup Per Hour: RM100",
            price,
            1, // packageId - you can get this from database
            1  // subPackageId - you can get this from database
        );
        startActivity(intent);
    }

    private String getEventsForPackage(String packageName) {
        switch (packageName) {
            case "PV 5":
            case "PV 4":
                return "Akad Nikah, Sanding & Tandang";
            case "PV 3":
            case "PV 2":
            case "PV 1":
                return "Akad Nikah / Sanding";
            default:
                return "Wedding Events";
        }
    }

    private String getEventDetailsForPackage(String packageName) {
        switch (packageName) {
            case "PV 5":
                return "(3 Day 3 Event)";
            case "PV 4":
                return "(2 Day 3 Event)";
            case "PV 3":
                return "(2 Day 2 Event)";
            case "PV 2":
                return "(1 Day 2 Event)";
            case "PV 1":
                return "(1 Day 1 Event)";
            default:
                return "(1 Day 1 Event)";
        }
    }

    private String getDeliverablesForPackage(String packageName) {
        switch (packageName) {
            case "PV 5":
                return "• 1x Full Shower (Akad & Sanding & Tandang)\n• 7-10 Minute Highlight Video\n• 1x Exclusive Pendrive 16 GB & Case";
            case "PV 4":
                return "• 1x Full Shower (Akad & Sanding & Tandang)\n• 6-9 Minute Highlight Video\n• 1x Exclusive Pendrive 16 GB & Case";
            case "PV 3":
            case "PV 2":
            case "PV 1":
                return "• 1x Full Showreel (Akad / Sanding)\n• 5-7 Minute Highlight Video\n• 1x Exclusive Pendrive 16 GB & Case";
            default:
                return "• Professional video production\n• High-quality editing\n• Digital delivery";
        }
    }
}
