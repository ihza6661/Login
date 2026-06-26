package com.ryan.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MahasiswaActivity extends AppCompatActivity {

    private TextView tvTotalMahasiswa;
    private MahasiswaAdapter adapter;
    private ArrayList<MahasiswaModel> mahasiswaList;
    private SwipeRefreshLayout swipeRefresh;

    private static final String API_URL = "https://stmikpontianak.cloud/011100862/tampilMahasiswa.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa);

        tvTotalMahasiswa = findViewById(R.id.tvTotalMahasiswa);
        RecyclerView rvMahasiswa = findViewById(R.id.rvMahasiswa);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        SearchView searchView = findViewById(R.id.searchView);

        if (rvMahasiswa != null) {
            rvMahasiswa.setLayoutManager(new LinearLayoutManager(this));
        }
        mahasiswaList = new ArrayList<>();
        adapter = new MahasiswaAdapter(mahasiswaList);
        if (rvMahasiswa != null) {
            rvMahasiswa.setAdapter(adapter);
        }

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchData);
        }

        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(MahasiswaActivity.this, TambahMahasiswaActivity.class);
                startActivity(intent);
            });
        }

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }

        fetchData();
    }

    private void fetchData() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) result.append(line);
                    reader.close();

                    JSONArray jsonArray = new JSONArray(result.toString());
                    ArrayList<MahasiswaModel> newList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        newList.add(new MahasiswaModel(
                                obj.getString("nim"),
                                obj.getString("nama"),
                                obj.getString("jenis_kelamin"),
                                obj.getString("jp")
                        ));
                    }

                    runOnUiThread(() -> {
                        mahasiswaList.clear();
                        mahasiswaList.addAll(newList);
                        adapter.updateList(newList);
                        tvTotalMahasiswa.setText(getString(R.string.total_count, mahasiswaList.size()));
                        if (swipeRefresh != null) {
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("MahasiswaActivity", "Error fetching mahasiswa data", e);
                runOnUiThread(() -> {
                    Toast.makeText(MahasiswaActivity.this, "Gagal sinkronisasi jaringan server!", Toast.LENGTH_LONG).show();
                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
}