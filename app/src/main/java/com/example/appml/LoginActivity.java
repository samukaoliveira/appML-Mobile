package com.example.appml;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appml.models.LoginResponse;
import com.example.appml.models.UsuarioLoginRequest;
import com.example.appml.services.ApiService;
import com.example.appml.services.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private TextView flashMessage;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        loginButton = findViewById(R.id.loginButton);
        flashMessage = findViewById(R.id.flashMessage);

        apiService = RetrofitInstance.getRetrofitInstance(this).create(ApiService.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                boolean rememberMe = rememberMeCheckBox.isChecked();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                    flashMessage.setText("Por favor, preencha todos os campos!");
                    return;
                }

                UsuarioLoginRequest request = new UsuarioLoginRequest(email, password);

                apiService.login(request).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String token = response.body().getToken();

                            SharedPreferences sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("auth_token", token);
                            editor.apply(); // ou editor.commit();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Erro: " + response.message(), Toast.LENGTH_SHORT).show();
                            flashMessage.setText(traduzErroLogin(response.message().toLowerCase()));
                        }
                    }

                    public String traduzErroLogin(String erro){
                        if (erro.contains("unauthorized")) {
                            return "Email e/ou senha inválidos!";
                        }
                        return erro;
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Erro de conexão: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        flashMessage.setText("Erro de conexão: " + t.getMessage());
                    }
                });
            }
        });
    }
}
