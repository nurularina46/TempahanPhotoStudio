package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SubPackageSingleActivity extends AppCompatActivity {
    private TextView tvPackageName, tvMainService, tvEvents, tvEventDetails, 
                     tvDuration, tvDeliverables, tvAddOns, tvPrice;
    private Button btnBookNow, btnAddToCart;
    private String packageName, mainService, events, eventDetails, duration, 
                   deliverables, addOns, price;
    private int packageId, subPackageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subpackage_single);

        // Get package data from intent
        getPackageData();

        // Initialize views
        initializeViews();

        // Setup click listeners
        setupClickListeners();

        // Populate data
        populateData();
    }

    private void getPackageData() {
        Intent intent = getIntent();
        packageName = intent.getStringExtra("PACKAGE_NAME");
        mainService = intent.getStringExtra("MAIN_SERVICE");
        events = intent.getStringExtra("EVENTS");
        eventDetails = intent.getStringExtra("EVENT_DETAILS");
        duration = intent.getStringExtra("DURATION");
        deliverables = intent.getStringExtra("DELIVERABLES");
        addOns = intent.getStringExtra("ADD_ONS");
        price = intent.getStringExtra("PRICE");
        packageId = intent.getIntExtra("PACKAGE_ID", 0);
        subPackageId = intent.getIntExtra("SUB_PACKAGE_ID", 0);

        // Set default values if not provided
        if (packageName == null) packageName = "PV 5";
        if (mainService == null) mainService = "3 Event 1 Video";
        if (events == null) events = "Akad Nikah, Sanding & Tandang";
        if (eventDetails == null) eventDetails = "(3 Day 3 Event)";
        if (duration == null) duration = "6-7 Hours Per Day";
        if (deliverables == null) deliverables = "• 1x Full Shower (Akad & Sanding & Tandang)\n• 7-10 Minute Highlight Video\n• 1x Exclusive Pendrive 16 GB & Case";
        if (addOns == null) addOns = "• Drone 1 Per Day: RM500\n• +1 Videographer Per Day: RM500\n• Raw Footage + 64GB: RM150\n• Persiapan/Makeup Per Hour: RM100";
        if (price == null) price = "RM 2800";
    }

    private void initializeViews() {
        tvPackageName = findViewById(R.id.tvPackageName);
        tvMainService = findViewById(R.id.tvMainService);
        tvEvents = findViewById(R.id.tvEvents);
        tvEventDetails = findViewById(R.id.tvEventDetails);
        tvDuration = findViewById(R.id.tvDuration);
        tvDeliverables = findViewById(R.id.tvDeliverables);
        tvAddOns = findViewById(R.id.tvAddOns);
        tvPrice = findViewById(R.id.tvPrice);
        btnBookNow = findViewById(R.id.btnBookNow);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Book Now button
        btnBookNow.setOnClickListener(v -> {
            // Navigate to booking details
            Intent intent = new Intent(this, BookingDetailsActivity.class);
            intent.putExtra("PACKAGE_ID", packageId);
            intent.putExtra("SUB_PACKAGE_ID", subPackageId);
            intent.putExtra("PACKAGE_NAME", packageName);
            intent.putExtra("PRICE", price);
            startActivity(intent);
        });

        // Add to Cart button
        btnAddToCart.setOnClickListener(v -> {
            // Add to cart functionality
            Toast.makeText(this, packageName + " added to cart!", Toast.LENGTH_SHORT).show();
            // You can implement cart functionality here
        });
    }

    private void populateData() {
        tvPackageName.setText(packageName);
        tvMainService.setText(mainService);
        tvEvents.setText(events);
        tvEventDetails.setText(eventDetails);
        tvDuration.setText(duration);
        tvDeliverables.setText(deliverables);
        tvAddOns.setText(addOns);
        tvPrice.setText(price);
    }

    // Method to create intent for this activity
    public static Intent createIntent(android.content.Context context, 
                                    String packageName, String mainService, 
                                    String events, String eventDetails, 
                                    String duration, String deliverables, 
                                    String addOns, String price, 
                                    int packageId, int subPackageId) {
        Intent intent = new Intent(context, SubPackageSingleActivity.class);
        intent.putExtra("PACKAGE_NAME", packageName);
        intent.putExtra("MAIN_SERVICE", mainService);
        intent.putExtra("EVENTS", events);
        intent.putExtra("EVENT_DETAILS", eventDetails);
        intent.putExtra("DURATION", duration);
        intent.putExtra("DELIVERABLES", deliverables);
        intent.putExtra("ADD_ONS", addOns);
        intent.putExtra("PRICE", price);
        intent.putExtra("PACKAGE_ID", packageId);
        intent.putExtra("SUB_PACKAGE_ID", subPackageId);
        return intent;
    }
}
