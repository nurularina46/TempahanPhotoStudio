package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {
    private TextView tvInvoiceNumber, tvPackageName, tvSubPackageClass, tvPrice, 
                     tvEventDate, tvPaymentMethod, tvBookingDate, tvStatus;
    private Button btnBackToDashboard, btnViewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Get intent data
        int bookingId = getIntent().getIntExtra("BOOKING_ID", 0);
        String packageName = getIntent().getStringExtra("PACKAGE_NAME");
        String subPackageClass = getIntent().getStringExtra("SUB_PACKAGE_CLASS");
        double price = getIntent().getDoubleExtra("PRICE", 0.0);
        String eventDate = getIntent().getStringExtra("EVENT_DATE");
        String paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");
        String role = getIntent().getStringExtra("ROLE");
        String username = getIntent().getStringExtra("USERNAME");

        // Initialize views
        tvInvoiceNumber = findViewById(R.id.tvInvoiceNumber);
        tvPackageName = findViewById(R.id.tvPackageName);
        tvSubPackageClass = findViewById(R.id.tvSubPackageClass);
        tvPrice = findViewById(R.id.tvPrice);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvStatus = findViewById(R.id.tvStatus);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
        btnViewHistory = findViewById(R.id.btnViewHistory);

        // Set invoice data
        tvInvoiceNumber.setText("INV-" + String.format("%06d", bookingId));
        tvPackageName.setText(packageName);
        tvSubPackageClass.setText(subPackageClass + " Package");
        tvPrice.setText("RM " + String.format("%.2f", price));
        tvEventDate.setText(eventDate);
        tvPaymentMethod.setText(paymentMethod);
        tvBookingDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
        tvStatus.setText("Pending Confirmation");

        // Set click listeners
        btnBackToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, PackageCategoryActivity.class);
            intent.putExtra("ROLE", role);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });

        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra("ROLE", role);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
    }
}
