package com.example.appml.services;

import com.example.appml.models.LoginResponse;
import com.example.appml.models.UsuarioLoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @POST("login/")
    Call<LoginResponse> login(@Body UsuarioLoginRequest usuario);
}