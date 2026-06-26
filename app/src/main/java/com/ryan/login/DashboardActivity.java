package com.ryan.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Menata posisi padding agar presisi di layar HP
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hubungkan teks sambutan nama user real
        TextView tvWelcomeDashboard = findViewById(R.id.tvWelcomeDashboard);
        String namaUserReal = getIntent().getStringExtra("KEY_USER");
        if (namaUserReal != null && tvWelcomeDashboard != null) {
            tvWelcomeDashboard.setText(getString(R.string.welcome_user, namaUserReal));
        }

        // Inisialisasi komponen Layout Menu berdasarkan ID XML Anda
        LinearLayout menuForex = findViewById(R.id.menuForex);
        LinearLayout menuMahasiswa = findViewById(R.id.menuMahasiswa);
        LinearLayout menuCuaca = findViewById(R.id.menuCuaca);

        // 1. Aksi Klik Menu Forex
        if (menuForex != null) {
            menuForex.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, ForexActivity.class);
                startActivity(intent);
            });
        }

        // 2. Aksi Klik Menu Master Mahasiswa
        if (menuMahasiswa != null) {
            menuMahasiswa.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, MahasiswaActivity.class);
                startActivity(intent);
            });
        }

        // 3. Aksi Klik Menu Cuaca
        if (menuCuaca != null) {
            menuCuaca.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, CuacaActivity.class);
                startActivity(intent);
            });
        }
    }
}