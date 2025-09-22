package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button loginBtn;
    TextView signupLink;
    ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize connection class
        connectionClass = new ConnectionClass();

        // Inisialisasi komponen UI
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupLink = findViewById(R.id.signupLink);

        // Klik butang login
        loginBtn.setOnClickListener(v -> {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                String result = connectionClass.validateUser(userEmail, userPassword);
                
                runOnUiThread(() -> {
                    if ("success".equals(result)) {
                        // Get user details in a separate thread with fresh connection
                        new Thread(() -> {
                            ConnectionClass freshConnection = new ConnectionClass();
                            UserModel user = freshConnection.getUserByEmail(userEmail);
                            
                            runOnUiThread(() -> {
                                if (user != null) {
                                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                                    // Redirect to Dashboard with user information
                                    Intent intent = new Intent(Login.this, Dashboard.class);
                                    intent.putExtra("ROLE", user.getRole());
                                    intent.putExtra("USERNAME", user.getUsername());
                                    startActivity(intent);
                                    finish(); // Supaya user tidak boleh tekan 'back' kembali ke Login
                                } else {
                                    Toast.makeText(this, "Failed to get user details. Please check if users table exists.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }).start();
                    } else {
                        Toast.makeText(this, "Login failed: " + result, Toast.LENGTH_LONG).show();
                    }
                });
            }).start();
        });

        // Klik link ke halaman register
        signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }
}
