package com.ryan.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

// ==========================================
// TAMBAHAN: Import untuk memproses JSON
// ==========================================
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText etId, etPassword;
    private AppCompatButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inisialisasi komponen form login sesuai XML Anda
        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Aksi ketika tombol Login ditekan
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idInput = etId.getText().toString().trim();
                String passInput = etPassword.getText().toString().trim();

                // Validasi agar tidak mengirim data kosong ke API
                if (idInput.isEmpty() || passInput.isEmpty()) {
                    Toast.makeText(MainActivity.this, "ID dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. Merakit URL
                String url = "https://stmikpontianak.cloud/011100862/login.php?id=" + idInput + "&password=" + passInput;
                Log.d("*tw*", "_url: " + url);


                // 2. Eksekusi request API
                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                asyncHttpClient.get(url, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        // Menangkap hasil dari server PHP
                        String hasil = new String(responseBody).trim();
                        Log.d("*tw*", "hasil: " + hasil);

                        try {
                            // 1. Karena hasil berupa JSON Array [ ... ], kita bongkar dulu
                            JSONArray jsonArray = new JSONArray(hasil);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            // 2. Ambil nilai "idCount" di dalamnya
                            String idCount = jsonObject.getString("idCount");

                            // 3. Validasi ketat: HANYA boleh lolos jika idCount bernilai "1"
                            if (idCount.equals("1")) {
                                // --- JIKA API MERESPON SUKSES (Data Cocok) ---

                                // Merakit Custom Toast Notifikasi
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

                                TextView text = layout.findViewById(R.id.tvToastText);
                                if (text != null) {
                                    // PERBAIKAN: Mengubah _id menjadi idInput sesuai variabel Anda di atas
                                    text.setText("Selamat Datang, " + idInput);
                                }

                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.BOTTOM, 0, 100);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();

                                // Berpindah halaman menuju DashboardActivity
                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                // PERBAIKAN: Mengubah _id menjadi idInput
                                intent.putExtra("KEY_USER", idInput);
                                startActivity(intent);
                                finish();

                            } else {
                                // --- JIKA idCount BERNILAI "0" (Username/Password Salah) ---
                                Toast.makeText(MainActivity.this, "Username atau Password salah!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // Mengantisipasi jika terjadi error format data
                            Log.e("*tw*", "Gagal membaca JSON", e);
                            Toast.makeText(MainActivity.this, "Format data server salah!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        // Menangkap error jika server down atau internet mati
                        Toast.makeText(MainActivity.this, "Koneksi ke server gagal: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("*tw*", "Error API: ", error);
                    }
                });
            }
        });
    }
}