package com.example.appml;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appml.LoginActivity;

public class BaseActivity extends AppCompatActivity {

    protected ImageButton btnHome;
    protected ImageButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        aplicarStatusBarTransparente();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupHeaderButtons();
    }

    private void aplicarStatusBarTransparente() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    /**
     * Configura automaticamente os botões do header (se existirem no layout)
     */
    private void setupHeaderButtons() {
        btnHome = findViewById(R.id.btnHome);
        btnLogout = findViewById(R.id.btnLogout);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }
    }

    /**
     * Logout global do app
     */
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da conta")
                .setMessage("Tem certeza que deseja sair?")
                .setCancelable(true)

                .setPositiveButton("Sair", (dialog, which) -> {
                    // limpa token
                    getSharedPreferences("AppPrefs", MODE_PRIVATE)
                            .edit()
                            .remove("auth_token")
                            .apply();

                    // vai pro login
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })

                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })

                .show();
    }
}