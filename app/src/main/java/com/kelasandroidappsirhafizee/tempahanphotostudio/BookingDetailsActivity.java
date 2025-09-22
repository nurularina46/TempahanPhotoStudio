package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingDetailsActivity extends AppCompatActivity {
    private TextView tvPackageName, tvSubPackageClass, tvPrice, tvDetails;
    private EditText etEventDate, etNotes;
    private Spinner spinnerPaymentMethod;
    private Button btnConfirmBooking;
    private ConnectionClass connectionClass;
    private String packageName, subPackageClass, category, role, username;
    private int packageId, subPackageId;
    private double price;
    private String details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        // Get intent data
        packageId = getIntent().getIntExtra("PACKAGE_ID", -1);
        packageName = getIntent().getStringExtra("PACKAGE_NAME");
        subPackageId = getIntent().getIntExtra("SUB_PACKAGE_ID", -1);
        subPackageClass = getIntent().getStringExtra("SUB_PACKAGE_CLASS");
        price = getIntent().getDoubleExtra("SUB_PACKAGE_PRICE", 0.0);
        details = getIntent().getStringExtra("SUB_PACKAGE_DETAILS");
        category = getIntent().getStringExtra("CATEGORY");
        role = getIntent().getStringExtra("ROLE");
        username = getIntent().getStringExtra("USERNAME");

        // Initialize views
        tvPackageName = findViewById(R.id.tvPackageName);
        tvSubPackageClass = findViewById(R.id.tvSubPackageClass);
        tvPrice = findViewById(R.id.tvPrice);
        tvDetails = findViewById(R.id.tvDetails);
        etEventDate = findViewById(R.id.etEventDate);
        etNotes = findViewById(R.id.etNotes);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        // Initialize connection class
        connectionClass = new ConnectionClass();

        // Set up spinner for payment methods
        String[] paymentMethods = {"Cash", "Credit Card", "Bank Transfer", "Online Payment"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        // Display package information
        tvPackageName.setText(packageName);
        tvSubPackageClass.setText(subPackageClass + " Package");
        tvPrice.setText("RM " + String.format("%.2f", price));
        tvDetails.setText(details);

        // Set click listener
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void confirmBooking() {
        String eventDate = etEventDate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

        if (eventDate.isEmpty()) {
            Toast.makeText(this, "Please select an event date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create booking
        BookingModel booking = new BookingModel();
        booking.setUserId(1); // In a real app, get from logged-in user
        booking.setPackageId(packageId);
        booking.setSubPackageId(subPackageId);
        booking.setBookingDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        booking.setEventDate(eventDate);
        booking.setStatus("Pending");
        booking.setTotalAmount(price);
        booking.setPaymentMethod(paymentMethod);
        booking.setPaymentStatus("Pending");
        booking.setNotes(notes);
        booking.setCreatedAt(new Date());

        new Thread(() -> {
            String result = connectionClass.addBooking(booking);
            runOnUiThread(() -> {
                if ("success".equals(result)) {
                    Toast.makeText(this, "Booking confirmed successfully!", Toast.LENGTH_SHORT).show();
                    
                    // Go to invoice/confirmation
                    Intent intent = new Intent(this, InvoiceActivity.class);
                    intent.putExtra("BOOKING_ID", 1); // In a real app, get the actual booking ID
                    intent.putExtra("PACKAGE_NAME", packageName);
                    intent.putExtra("SUB_PACKAGE_CLASS", subPackageClass);
                    intent.putExtra("PRICE", price);
                    intent.putExtra("EVENT_DATE", eventDate);
                    intent.putExtra("PAYMENT_METHOD", paymentMethod);
                    intent.putExtra("ROLE", role);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Failed to confirm booking: " + result, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
