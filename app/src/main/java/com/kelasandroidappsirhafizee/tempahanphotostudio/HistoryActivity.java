package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    private Button btnUpcoming, btnPast;
    private RecyclerView recyclerViewBookings;
    private LinearLayout layoutEmptyState;
    private ConnectionClass connectionClass;
    private ArrayList<BookingModel> allBookings;
    private ArrayList<BookingModel> upcomingBookings;
    private ArrayList<BookingModel> pastBookings;
    private BookingHistoryAdapter adapter;
    private String currentTab = "upcoming";
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Get user ID from intent
        userId = getIntent().getIntExtra("USER_ID", 1); // Default to 1 if not provided

        // Initialize views
        btnUpcoming = findViewById(R.id.btnUpcoming);
        btnPast = findViewById(R.id.btnPast);
        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        // Initialize connection class
        connectionClass = new ConnectionClass();

        // Setup RecyclerView
        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));

        // Setup click listeners
        btnUpcoming.setOnClickListener(v -> switchTab("upcoming"));
        btnPast.setOnClickListener(v -> switchTab("past"));

        // Setup back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Load bookings
        loadBookings();
    }

    private void loadBookings() {
        new Thread(() -> {
            allBookings = connectionClass.getBookingsByUserId(userId);
            
            runOnUiThread(() -> {
                if (allBookings == null || allBookings.isEmpty()) {
                    showEmptyState();
                    return;
                }

                // Separate upcoming and past bookings
                separateBookings();
                
                // Show initial tab
                switchTab(currentTab);
            });
        }).start();
    }

    private void separateBookings() {
        upcomingBookings = new ArrayList<>();
        pastBookings = new ArrayList<>();
        
        Date currentDate = new Date();
        
        for (BookingModel booking : allBookings) {
            // Simple logic: if event date is in the future, it's upcoming
            // You might want to improve this date comparison logic
            if (booking.getEventDate() != null && !booking.getEventDate().isEmpty()) {
                // For now, we'll use a simple approach
                // In a real app, you'd parse the date and compare properly
                if (booking.getStatus().equalsIgnoreCase("confirmed") || 
                    booking.getStatus().equalsIgnoreCase("pending")) {
                    upcomingBookings.add(booking);
                } else {
                    pastBookings.add(booking);
                }
            } else {
                pastBookings.add(booking);
            }
        }
    }

    private void switchTab(String tab) {
        currentTab = tab;
        
        // Update button states
        if (tab.equals("upcoming")) {
            btnUpcoming.setSelected(true);
            btnPast.setSelected(false);
            showBookings(upcomingBookings);
        } else {
            btnUpcoming.setSelected(false);
            btnPast.setSelected(true);
            showBookings(pastBookings);
        }
    }

    private void showBookings(ArrayList<BookingModel> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            adapter = new BookingHistoryAdapter(bookings, this::onBookingClick);
            recyclerViewBookings.setAdapter(adapter);
        }
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerViewBookings.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        layoutEmptyState.setVisibility(View.GONE);
        recyclerViewBookings.setVisibility(View.VISIBLE);
    }

    private void onBookingClick(BookingModel booking) {
        // Handle booking click - you can navigate to booking details
        Toast.makeText(this, "Booking clicked: " + booking.getId(), Toast.LENGTH_SHORT).show();
        
        // Example: Navigate to booking details
        // Intent intent = new Intent(this, BookingDetailsActivity.class);
        // intent.putExtra("BOOKING_ID", booking.getId());
        // startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh bookings when returning to this activity
        loadBookings();
    }
}