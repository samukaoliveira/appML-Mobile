package com.example.appml.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.appml.LoginActivity;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class RetrofitInstance {

//    private static final String BASE_URL = "http://134.255.176.164:49152/api/";
    protected static final String BASE_URL = "http://134.255.176.164:49153/api/";
//    private static final String BASE_URL = "http://192.168.1.104:3000/api/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            SharedPreferences sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                            String token = sharedPref.getString("auth_token", null);

                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder();

                            if (token != null) {
                                requestBuilder.header("Authorization", "Bearer " + token);
                            }

                            Request request = requestBuilder.build();
                            Response response = chain.proceed(request);

                            if (response.code() == 401) {
                                // limpa token
                                sharedPref.edit().remove("auth_token").apply();

                                // redireciona para login
                                redirectToLogin(context);
                            }

                            return response;
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    private static void redirectToLogin(Context context) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }
}
