package com.example.argenbooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvUserEmail, tvApiStatus;
    private Button btnLogout;
    private LinearLayout cardPalermoLoft;

    private FirebaseAuth mAuth;
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Проверка авторизации
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvApiStatus = findViewById(R.id.tvApiStatus);
        btnLogout = findViewById(R.id.btnLogout);
        cardPalermoLoft = findViewById(R.id.cardPalermoLoft);

        httpClient = new OkHttpClient();

        if (currentUser.getEmail() != null) {
            tvUserEmail.setText(currentUser.getEmail());
        }

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        cardPalermoLoft.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("PROPERTY_NAME", "Palermo Luxury Loft");
            intent.putExtra("PRICE_PER_NIGHT", 120);
            startActivity(intent);
        });

        fetchExchangeRate();
    }

    // Запрос к стороннему API курсов валют (OkHttp + Gson)
    private void fetchExchangeRate() {
        String url = "https://api.exchangerate-api.com/v4/latest/USD";

        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        tvApiStatus.setText("API: No se pudo cargar la cotización"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    JsonObject rates = jsonObject.getAsJsonObject("rates");

                    if (rates.has("ARS")) {
                        double arsRate = rates.get("ARS").getAsDouble();
                        new Handler(Looper.getMainLooper()).post(() ->
                                tvApiStatus.setText(String.format("Cotización Oficial: 1 USD = %.2f ARS (API Externa)", arsRate)));
                    }
                }
            }
        });
    }
}