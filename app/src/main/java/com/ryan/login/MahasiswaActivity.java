package com.ryan.login;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MahasiswaActivity extends AppCompatActivity {

    private RecyclerView rvMahasiswa;
    private TextView tvTotalMahasiswa;
    private MahasiswaAdapter adapter;
    private ArrayList<MahasiswaModel> mahasiswaList;
    private SearchView searchView;
    private ImageButton btnRefresh, btnTambah;

    private static final String API_URL = "https://stmikpontianak.cloud/011100862/tampilMahasiswa.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa);

        // Inisialisasi Komponen
        rvMahasiswa = findViewById(R.id.rvMahasiswa);
        tvTotalMahasiswa = findViewById(R.id.tvTotalMahasiswa);
        searchView = findViewById(R.id.searchView);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnTambah = findViewById(R.id.btnTambah);

        rvMahasiswa.setLayoutManager(new LinearLayoutManager(this));
        mahasiswaList = new ArrayList<>();
        adapter = new MahasiswaAdapter(mahasiswaList);
        rvMahasiswa.setAdapter(adapter);

        // Listener Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }
        });

        // Listener Tombol dengan Umpan Balik (Toast)
        btnRefresh.setOnClickListener(v -> {
            Toast.makeText(this, "Memperbarui data...", Toast.LENGTH_SHORT).show();
            ambilDataMhsDariServer();
        });

        btnTambah.setOnClickListener(v -> {
            Intent intent = new Intent(MahasiswaActivity.this, TambahMahasiswaActivity.class);
            startActivity(intent);
        });

        ambilDataMhsDariServer();
    }

    private void filterData(String text) {
        ArrayList<MahasiswaModel> filteredList = new ArrayList<>();
        for (MahasiswaModel item : mahasiswaList) {
            if (item.getNama().toLowerCase().contains(text.toLowerCase()) ||
                    item.getNim().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter = new MahasiswaAdapter(filteredList);
        rvMahasiswa.setAdapter(adapter);
    }

    private void ambilDataMhsDariServer() {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) result.append(line);
                    reader.close();

                    JSONArray jsonArray = new JSONArray(result.toString());
                    mahasiswaList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String nim = obj.optString("NIM", "-");
                        String nama = obj.optString("Nama", "-");
                        String jk = obj.optString("JenisKelamin", "Laki-Laki");
                        String jp = obj.optString("JP", "TI");
                        String jFix = jp.toUpperCase().contains("SI") ? "SI" : "TI";
                        mahasiswaList.add(new MahasiswaModel(nim, nama, jk, jFix));
                    }

                    runOnUiThread(() -> {
                        adapter = new MahasiswaAdapter(mahasiswaList);
                        rvMahasiswa.setAdapter(adapter);
                        tvTotalMahasiswa.setText("Total : " + mahasiswaList.size());
                        Toast.makeText(MahasiswaActivity.this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MahasiswaActivity.this, "Gagal sinkronisasi jaringan server!", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}