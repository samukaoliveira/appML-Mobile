package com.example.appml.services;

import com.example.appml.models.escala.Escala;
import com.example.appml.models.LoginResponse;
import com.example.appml.models.UsuarioLoginRequest;
import com.example.appml.models.escala.EscalaDetalhada;
import com.example.appml.models.escala.EscalaSimples;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("login/")
    Call<LoginResponse> login(@Body UsuarioLoginRequest usuario);

    @GET("escalas/")
    Call<List<EscalaSimples>> getListaEscalas();

    @GET("escalas/{id}")
    Call<EscalaDetalhada> getDetalheEscala(@Path("id") int id);
}