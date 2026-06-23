package com.ryan.login;

import android.os.Bundle;
import android.util.Log; // Tambahkan ini untuk debugging
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Import library AsyncHttpClient
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cz.msebera.android.httpclient.Header;
import java.net.URLEncoder;
import java.util.Map;

public class TambahMahasiswaActivity extends AppCompatActivity {

    private EditText etNim, etNama, etTempatLahir, etTanggalLahir, etAlamat, etTahunMasuk;
    private Spinner spJenisKelamin, spJurusan, spStatusNikah;
    private Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_mahasiswa);

        // Inisialisasi View
        etNim = findViewById(R.id.etNim);
        etNama = findViewById(R.id.etNama);
        etTempatLahir = findViewById(R.id.etTempatLahir);
        etTanggalLahir = findViewById(R.id.etTanggalLahir);
        etAlamat = findViewById(R.id.etAlamat);
        etTahunMasuk = findViewById(R.id.etTahunMasuk);
        spJenisKelamin = findViewById(R.id.spJenisKelamin);
        spJurusan = findViewById(R.id.spJurusan);
        spStatusNikah = findViewById(R.id.spStatusNikah);
        btnSimpan = findViewById(R.id.btnSimpan);

        setupSpinner(spJenisKelamin, new String[]{"Laki-laki", "Perempuan"});
        setupSpinner(spJurusan, new String[]{"TI", "SI"});
        setupSpinner(spStatusNikah, new String[]{"Menikah", "Belum Menikah"});

        btnSimpan.setOnClickListener(v -> simpanDataKeServer());
    }

    private void setupSpinner(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);
    }

    private void simpanDataKeServer() {
        try {
            // 1. Susun URL dengan encoding yang benar
            String url = "https://stmikpontianak.cloud/011100862/tambahMahasiswa.php?" +
                    "nim=" + URLEncoder.encode(etNim.getText().toString(), "UTF-8") +
                    "&nama=" + URLEncoder.encode(etNama.getText().toString(), "UTF-8") +
                    "&jenisKelamin=" + URLEncoder.encode(spJenisKelamin.getSelectedItem().toString(), "UTF-8") +
                    "&tempatLahir=" + URLEncoder.encode(etTempatLahir.getText().toString(), "UTF-8") +
                    "&tanggalLahir=" + URLEncoder.encode(etTanggalLahir.getText().toString(), "UTF-8") +
                    "&statusPernikahan=" + URLEncoder.encode(spStatusNikah.getSelectedItem().toString(), "UTF-8") +
                    "&alamat=" + URLEncoder.encode(etAlamat.getText().toString(), "UTF-8") +
                    "&jp=" + URLEncoder.encode(spJurusan.getSelectedItem().toString(), "UTF-8") +
                    "&tahunMasuk=" + URLEncoder.encode(etTahunMasuk.getText().toString(), "UTF-8");

            // 2. Kirim Request
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    Log.d("API_DEBUG", "Respon: " + response); // Cek ini di Logcat

                    // 3. Parsing JSON Respon
                    try {
                        Gson gson = new Gson();
                        Map<String, String> map = gson.fromJson(response, new TypeToken<Map<String, String>>(){}.getType());

                        if ("ok".equals(map.get("status"))) {
                            Toast.makeText(TambahMahasiswaActivity.this, "Berhasil: " + map.get("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(TambahMahasiswaActivity.this, "Server: " + map.get("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(TambahMahasiswaActivity.this, "Format JSON salah", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(TambahMahasiswaActivity.this, "Gagal koneksi server", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}