package com.ryan.login;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // Import Baru

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class ForexActivity extends AppCompatActivity {

    private TextView tvForexDate;
    private RecyclerView rvForex;
    private ForexAdapter adapter;
    private ArrayList<ForexModel> forexDataList;
    private HashMap<String, String> dynamicNamesMap;

    // Variabel baru untuk mendeteksi swipe refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String APP_ID = "4c2a874b5ccf40d59aae08ecd0bb37db";
    private static final String NAMES_URL = "https://openexchangerates.org/api/currencies.json";
    private static final String RATES_URL = "https://openexchangerates.org/api/latest.json?app_id=" + APP_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forex);

        tvForexDate = findViewById(R.id.tvForexDate);
        rvForex = findViewById(R.id.rvForex);
        rvForex.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi komponen SwipeRefreshLayout dari XML
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        forexDataList = new ArrayList<>();
        dynamicNamesMap = new HashMap<>();

        adapter = new ForexAdapter(forexDataList);
        rvForex.setAdapter(adapter);

        // Pasang fungsi listener saat layar ditarik/digulir ke atas
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Jalankan kembali fungsi ambil data dari internet
                fetchDataFromAPIs();
            }
        });

        // Ambil data pertama kali saat halaman dibuka
        fetchDataFromAPIs();
    }

    private void fetchDataFromAPIs() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // ---- TAHAP 1: AMBIL DATA NAMA MATA UANG ----
                    URL urlNames = new URL(NAMES_URL);
                    HttpURLConnection connNames = (HttpURLConnection) urlNames.openConnection();
                    connNames.setRequestMethod("GET");

                    BufferedReader readerNames = new BufferedReader(new InputStreamReader(connNames.getInputStream()));
                    StringBuilder resNames = new StringBuilder();
                    String lineNames;
                    while ((lineNames = readerNames.readLine()) != null) {
                        resNames.append(lineNames);
                    }
                    readerNames.close();

                    JSONObject jsonNames = new JSONObject(resNames.toString());
                    Iterator<String> nameKeys = jsonNames.keys();
                    while (nameKeys.hasNext()) {
                        String code = nameKeys.next();
                        String fullName = jsonNames.getString(code);
                        dynamicNamesMap.put(code, fullName);
                    }

                    // ---- TAHAP 2: AMBIL DATA NILAI KURS ----
                    URL urlRates = new URL(RATES_URL);
                    HttpURLConnection connRates = (HttpURLConnection) urlRates.openConnection();
                    connRates.setRequestMethod("GET");

                    BufferedReader readerRates = new BufferedReader(new InputStreamReader(connRates.getInputStream()));
                    StringBuilder resRates = new StringBuilder();
                    String lineRates;
                    while ((lineRates = readerRates.readLine()) != null) {
                        resRates.append(lineRates);
                    }
                    readerRates.close();

                    JSONObject jsonRatesMain = new JSONObject(resRates.toString());
                    long timestamp = jsonRatesMain.getLong("timestamp");
                    JSONObject ratesObject = jsonRatesMain.getJSONObject("rates");

                    Date date = new Date(timestamp * 1000L);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedDate = "Tanggal dan Waktu: " + sdf.format(date);

                    ArrayList<ForexModel> loadedList = new ArrayList<>();
                    Iterator<String> rateKeys = ratesObject.keys();
                    while (rateKeys.hasNext()) {
                        String code = rateKeys.next();
                        double rateValue = ratesObject.getDouble(code);

                        String fullName = dynamicNamesMap.getOrDefault(code, "Foreign Currency");
                        String formattedRate = String.format(Locale.US, "%,.2f", rateValue);

                        loadedList.add(new ForexModel(code, fullName, formattedRate));
                    }

                    // Transisikan data kembali untuk memperbarui UI Layar HP
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvForexDate.setText(formattedDate);
                            forexDataList.clear();
                            forexDataList.addAll(loadedList);
                            adapter.notifyDataSetChanged();

                            // Selesai loading, matikan putaran icon refresh di layar
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(ForexActivity.this, "Data Kurs Diperbarui!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvForexDate.setText("Gagal sinkronisasi data.");
                            Toast.makeText(ForexActivity.this, "Koneksi API bermasalah!", Toast.LENGTH_SHORT).show();

                            // Tetap matikan putaran icon refresh meskipun proses gagal/error
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }).start();
    }
}