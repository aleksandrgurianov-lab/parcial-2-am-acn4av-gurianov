package com.example.argenbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private Button btnRegister, btnGoToLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Ingrese su nombre");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese su email");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        // 1. Создание пользователя в Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Сохраняем профиль в Firestore
                            saveUserProfile(user.getUid(), fullName, email);

                            // СРАЗУ делаем редирект на главный экран (MainActivity)
                            Toast.makeText(RegisterActivity.this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Error al registrar";
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // 2. Сохранение данных профиля в Firestore (Коллекция "users")
    private void saveUserProfile(String uid, String fullName, String email) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", uid);
        userMap.put("fullName", fullName);
        userMap.put("email", email);

        db.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();
                    // Переходим на главный экран-каталог MainActivity
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Если запись в Firestore не удалась, информируем и ВСЁ РАВНО пускаем дальше
                    Toast.makeText(RegisterActivity.this, "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}