package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Register extends AppCompatActivity {

    EditText etname, etemail, etpassword, etconfirmPassword;
    Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etname = findViewById(R.id.name);
        etemail = findViewById(R.id.email);
        etpassword = findViewById(R.id.password);
        etconfirmPassword = findViewById(R.id.confirmPassword);
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(v -> {
            registerUser();
        });
    }

    public void registerUser() {
        String name = etname.getText().toString();
        String email = etemail.getText().toString();
        String password = etpassword.getText().toString();
        String confirmPassword = etconfirmPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            ConnectionClass connectionClass = new ConnectionClass();
            String result = connectionClass.registerUser(name, email, password);
            runOnUiThread(() -> {
                if ("success".equals(result)) {
                    Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show();
                    etname.setText("");
                    etemail.setText("");
                    etpassword.setText("");
                    etconfirmPassword.setText("");
                } else {
                    Toast.makeText(this, "Registration failed: " + result, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}
