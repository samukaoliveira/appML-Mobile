package com.example.appml;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        loginButton = findViewById(R.id.loginButton);

        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                boolean rememberMe = rememberMeCheckBox.isChecked();

                // Validate fields
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Send login request to the backend (Rails API)
                    loginUser(email, password, rememberMe);
                }
            }
        });
    }

    private void loginUser(String email, String password, boolean rememberMe) {
        // Aqui você pode usar uma biblioteca como Retrofit ou Volley para fazer a requisição ao backend Rails
        // Para simplificação, vou apenas exibir um toast simulando a autenticação
        Toast.makeText(this, "Tentando logar como " + email, Toast.LENGTH_SHORT).show();

        // Exemplo de onde você pode fazer a autenticação via API
        // Exemplo usando Retrofit:
        // apiService.login(email, password).enqueue(new Callback<LoginResponse>() {
        //     @Override
        //     public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        //         if (response.isSuccessful()) {
        //             // Usuário autenticado com sucesso, redirecionar ou fazer algo
        //         } else {
        //             // Erro de autenticação
        //         }
        //     }
        //
        //     @Override
        //     public void onFailure(Call<LoginResponse> call, Throwable t) {
        //         Toast.makeText(LoginActivity.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
        //     }
        // });
    }
}