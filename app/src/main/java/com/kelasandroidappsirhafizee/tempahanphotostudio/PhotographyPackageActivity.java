package com.kelasandroidappsirhafizee.tempahanphotostudio;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhotographyPackageActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PackageAdapter adapter;
    PackageModel model;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photography_package);

        recyclerView = findViewById(R.id.recyclerViewPhotography);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}