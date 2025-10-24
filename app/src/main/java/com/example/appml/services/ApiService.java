package com.example.appml.services;

import com.example.appml.models.escala.Escala;
import com.example.appml.models.LoginResponse;
import com.example.appml.models.UsuarioLoginRequest;
import com.example.appml.models.escala.EscalaDetalhada;
import com.example.appml.models.escala.EscalaNotificacao;
import com.example.appml.models.escala.EscalaSimples;
import com.example.appml.models.mensalidade.Mensalidade;
import com.example.appml.models.musica.Musica;
import com.example.appml.models.musica.MusicaDetalhada;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("login/")
    Call<LoginResponse> login(@Body UsuarioLoginRequest usuario);

    @GET("escalas/")
    Call<List<EscalaSimples>> getListaEscalas();

    @GET("minhas_escalas/")
    Call<List<EscalaSimples>> getMinhasEscalas();

    @GET("musicas/")
    Call<List<Musica>> getMusicas();

    @GET("musicas/{id}")
    Call<MusicaDetalhada> getDetalheMusica(@Path("id") int id);

    @GET("escalas/{id}")
    Call<EscalaDetalhada> getDetalheEscala(@Path("id") int id);

    @GET("atualizadas")
    Call<List<EscalaNotificacao>> getAtualizadas();

    @GET("recentes")
    Call<List<EscalaNotificacao>> getRecentes();

    @GET("mensalidades")
    Call<List<Mensalidade>> getMensalidades();
}