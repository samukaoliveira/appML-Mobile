package com.example.appml.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private static final String VERSION_URL = RetrofitInstance.BASE_URL + "/app_update";
    private Context context;

    public UpdateChecker(Context context) {
        this.context = context;
    }

    public void checkForUpdate() {
        new Thread(() -> {
            try {
                URL url = new URL(VERSION_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                JSONObject json = new JSONObject(sb.toString());
                int serverVersionCode = json.getInt("versionCode");
                String apkUrl = json.getString("apkUrl");

                int currentVersionCode = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionCode;

                if (serverVersionCode > currentVersionCode) {
                    downloadAndInstall(apkUrl);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void downloadAndInstall(String apkUrl) {
        try {
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

            installApk(apkFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", apkFile);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}

