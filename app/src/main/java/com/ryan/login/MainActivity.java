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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText etId, etPassword;

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

        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        AppCompatButton btnLogin = findViewById(R.id.btnLogin);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String idInput = etId.getText().toString().trim();
                String passInput = etPassword.getText().toString().trim();

                if (idInput.isEmpty() || passInput.isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.login_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "https://stmikpontianak.cloud/011100862/login.php?id=" + idInput + "&password=" + passInput;
                Log.d("*tw*", "_url: " + url);

                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                asyncHttpClient.get(url, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String hasil = new String(responseBody).trim();
                        Log.d("*tw*", "hasil: " + hasil);

                        try {
                            JSONArray jsonArray = new JSONArray(hasil);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            String idCount = jsonObject.getString("idCount");

                            if (Objects.equals(idCount, "1")) {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

                                TextView text = layout.findViewById(R.id.tvToastText);
                                if (text != null) {
                                    text.setText(getString(R.string.welcome_user, idInput));
                                }

                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.BOTTOM, 0, 100);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();

                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                intent.putExtra("KEY_USER", idInput);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.e("*tw*", "Gagal membaca JSON", e);
                            Toast.makeText(MainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(MainActivity.this, getString(R.string.connection_failed, error.getMessage()), Toast.LENGTH_SHORT).show();
                        Log.e("*tw*", "Error API: ", error);
                    }
                });
            });
        }
    }
}