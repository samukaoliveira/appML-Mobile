package com.example.appml.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Classe responsável por verificar atualizações do app e instalar automaticamente.
 * Usa um endpoint JSON no formato:
 * {
 *   "versionCode": 6,
 *   "versionName": "1.6.1",
 *   "apkUrl": "https://meusite.com/meuapp-v1.6.1.apk"
 * }
 */
public class UpdateChecker {

    private static final String TAG = "UpdateChecker";
    private static final String VERSION_URL = RetrofitInstance.BASE_URL + "app_update";

    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public UpdateChecker(Context context) {
        this.context = context;
    }

    public void checkForUpdate() {
        new Thread(() -> {
            try {
                Log.d(TAG, "🔍 Iniciando verificação de atualização em: " + VERSION_URL);

                URL url = new URL(VERSION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);

                // 🔑 Adiciona o token salvo no SharedPreferences
                String token = getAuthToken();
                if (token != null && !token.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                    Log.d(TAG, "✅ Token adicionado à requisição.");
                } else {
                    Log.w(TAG, "⚠️ Nenhum token encontrado! A requisição pode falhar com 401.");
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "📡 Código de resposta HTTP: " + responseCode);

                if (responseCode == 401) {
                    showToast("Não autorizado. Faça login novamente.");
                    return;
                } else if (responseCode != 200) {
                    showToast("Erro ao verificar atualização (" + responseCode + ")");
                    return;
                }

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                Log.d(TAG, "📦 Resposta JSON: " + sb);

                JSONObject json = new JSONObject(sb.toString());
                int serverVersionCode = json.getInt("versionCode");
                String versionName = json.getString("versionName");
                String apkUrl = json.getString("apkUrl");

                int currentVersionCode = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionCode;

                Log.d(TAG, "📲 Versão local: " + currentVersionCode + " | Servidor: " + serverVersionCode);

                if (serverVersionCode > currentVersionCode) {
                    Log.i(TAG, "🆕 Nova versão detectada: " + versionName);
                    promptUserToUpdate(apkUrl, versionName);
                } else {
                    Log.d(TAG, "✅ Aplicativo já está atualizado.");
                    showToast("Aplicativo já está atualizado.");
                }

            } catch (Exception e) {
                Log.e(TAG, "💥 Erro ao verificar atualização: " + e.getMessage(), e);
                showToast("Erro ao verificar atualização.");
            }
        }).start();
    }

    private void promptUserToUpdate(String apkUrl, String versionName) {
        mainHandler.post(() -> {
            new AlertDialog.Builder(context)
                    .setTitle("Nova versão disponível")
                    .setMessage("A versão " + versionName + " está disponível. Deseja atualizar agora?")
                    .setCancelable(true)
                    .setPositiveButton("Atualizar", (dialog, which) -> {
                        showToast("Baixando atualização...");
                        new Thread(() -> downloadAndInstall(apkUrl)).start();
                    })
                    .setNegativeButton("Mais tarde", null)
                    .show();
        });
    }

    private void downloadAndInstall(String apkUrl) {
        try {
            Log.d(TAG, "⬇️ Iniciando download: " + apkUrl);

            URL url = new URL(apkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            File apkFile = new File(context.getExternalFilesDir(null), "update.apk");
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(apkFile);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) fos.write(buffer, 0, len);
            fos.close();
            is.close();

            Log.d(TAG, "✅ Download concluído: " + apkFile.getAbsolutePath());
            showToast("Download concluído. Instalando...");

            installApk(apkFile);

        } catch (Exception e) {
            Log.e(TAG, "💥 Erro ao baixar atualização: " + e.getMessage(), e);
            showToast("Erro no download da atualização.");
        }
    }

    private void installApk(File apkFile) {
        try {
            Uri apkUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    apkFile
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);

            Log.i(TAG, "🚀 Instalador iniciado com sucesso: " + apkUri);

        } catch (Exception e) {
            Log.e(TAG, "❌ Erro ao instalar APK: " + e.getMessage(), e);
            showToast("Erro ao iniciar instalação.");
        }
    }

    private void showToast(String message) {
        mainHandler.post(() ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        );
    }

    /** 🔐 Lê o token do SharedPreferences "AppPrefs" */
    private String getAuthToken() {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        Log.d(TAG, "Token carregado: " + (token != null ? "SIM" : "NÃO"));
        return token;
    }
}
