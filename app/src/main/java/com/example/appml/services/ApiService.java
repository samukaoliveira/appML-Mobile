package com.example.appml.services;

import com.example.appml.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("sessions")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);
}