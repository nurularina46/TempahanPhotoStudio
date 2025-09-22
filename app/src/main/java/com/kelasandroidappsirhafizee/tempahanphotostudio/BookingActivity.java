package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    Spinner spinnerPakej;
    EditText etDate, etTime;
    Button btnSendOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);

        spinnerPakej = findViewById(R.id.spinnerPakej);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        btnSendOrder = findViewById(R.id.btnSendOrder);

        // Senarai Pakej
        String[] pakejList = {"PV 1", "PV 2", "PV 3", "PV 4", "PV 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, pakejList);
        spinnerPakej.setAdapter(adapter);

        // Pilih Tarikh
        etDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Pilih Masa
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                etTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        // Butang Hantar
        btnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pakej = spinnerPakej.getSelectedItem().toString();
                String date = etDate.getText().toString();
                String time = etTime.getText().toString();

                if (date.isEmpty() || time.isEmpty()) {
                    Toast.makeText(BookingActivity.this,
                            "Sila isi semua maklumat", Toast.LENGTH_SHORT).show();
                } else {
                    // Untuk testing, kita hanya tunjuk Toast
                    Toast.makeText(BookingActivity.this,
                            "Booking Sent!\nPakej: " + pakej + "\nDate: " + date + "\nTime: " + time,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
