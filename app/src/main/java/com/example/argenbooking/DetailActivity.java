package com.example.argenbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String propertyName = "Palermo Luxury Loft";
    private int priceNight = 120;
    private String selectedStartDate = "";
    private long selectedNights = 0;
    private long totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // datos de MainActivity ( Intent )
        if (getIntent() != null) {
            if (getIntent().hasExtra("PROPERTY_NAME")) {
                propertyName = getIntent().getStringExtra("PROPERTY_NAME");
            }
            priceNight = getIntent().getIntExtra("PRICE_PER_NIGHT", 120);
        }

        // return a MainActivity
        TextView txtBreadcrumbs = findViewById(R.id.txtBreadcrumbs);
        if (txtBreadcrumbs != null) {
            txtBreadcrumbs.setOnClickListener(v -> {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }


        //Imagen de hotel
        ImageView imgLoft = findViewById(R.id.imgLoft);
        String loftImageUrl = "https://images.unsplash.com/photo-1505873242700-f289a29e1e0f?q=80&w=1176&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=NHwxMjA3fDA%3D";

        if (imgLoft != null) {
            Glide.with(this)
                    .load(loftImageUrl)
                    .into(imgLoft);
        }




        EditText inputCheckIn = findViewById(R.id.inputCheckIn);
        EditText inputNights = findViewById(R.id.inputNights);
        TextView lblCalculationDetails = findViewById(R.id.lblCalculationDetails);
        TextView lblTotalResult = findViewById(R.id.lblTotalResult);

        // MaterialDatePicker
        if (inputCheckIn != null && inputNights != null) {
            inputCheckIn.setOnClickListener(v -> {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Seleccione fechas de estadía");
                final MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();

                materialDatePicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");

                materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                    Long startDateMs = selection.first;
                    Long endDateMs = selection.second;

                    if (startDateMs != null && endDateMs != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        selectedStartDate = sdf.format(new Date(startDateMs));
                        inputCheckIn.setText(selectedStartDate);

                        long differenceMs = endDateMs - startDateMs;
                        selectedNights = differenceMs / (1000 * 60 * 60 * 24);
                        if (selectedNights < 1) {
                            selectedNights = 1;
                        }
                        inputNights.setText(String.valueOf(selectedNights));

                        totalAmount = priceNight * selectedNights;

                        if (lblCalculationDetails != null && lblTotalResult != null) {
                            lblCalculationDetails.setText(String.format(Locale.getDefault(), "$%d x %d noches", priceNight, selectedNights));
                            lblTotalResult.setText(String.format(Locale.getDefault(), "TOTAL: $%d", totalAmount));
                        }
                    }
                });
            });
        }



        // Reserva BTN
        Button btnReserve = findViewById(R.id.btnReserve);
        LinearLayout dynamicInvoiceContainer = findViewById(R.id.dynamicInvoiceContainer);

        if (btnReserve != null && dynamicInvoiceContainer != null) {
            btnReserve.setOnClickListener(v -> {
                if (selectedNights <= 0 || selectedStartDate.isEmpty()) {
                    Toast.makeText(DetailActivity.this, "Por favor, seleccione las fechas de estadía primero", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnReserve.setEnabled(false);
                btnReserve.setText("Procesando...");

                saveBookingToFirestore(btnReserve, dynamicInvoiceContainer);
            });
        }
    }

    private void saveBookingToFirestore(Button btnReserve, LinearLayout dynamicInvoiceContainer) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "anonymous";
        String userEmail = (currentUser != null && currentUser.getEmail() != null) ? currentUser.getEmail() : "sin_email";

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("userId", userId);
        bookingData.put("userEmail", userEmail);
        bookingData.put("propertyName", propertyName);
        bookingData.put("checkInDate", selectedStartDate);
        bookingData.put("nights", selectedNights);
        bookingData.put("pricePerNight", priceNight);
        bookingData.put("totalAmount", totalAmount);
        bookingData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("bookings")
                .add(bookingData)
                .addOnSuccessListener(documentReference -> {
                    dynamicInvoiceContainer.removeAllViews();

                    TextView dynamicCheck = new TextView(DetailActivity.this);
                    dynamicCheck.setText(String.format(Locale.getDefault(),
                            "¡Reserva confirmada en la nube!\nID: %s\nTotal: $%d USD",
                            documentReference.getId().substring(0, 6).toUpperCase(),
                            totalAmount));
                    dynamicCheck.setTextColor(ContextCompat.getColor(DetailActivity.this, R.color.orange_premium));
                    dynamicCheck.setTextSize(15);

                    dynamicInvoiceContainer.addView(dynamicCheck);

                    btnReserve.setText("Confirmado");
                    Toast.makeText(DetailActivity.this, "Reserva guardada en Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    btnReserve.setEnabled(true);
                    btnReserve.setText("Reservar");
                    Toast.makeText(DetailActivity.this, "Error al guardar reserva: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}