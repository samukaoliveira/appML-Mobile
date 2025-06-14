package com.example.appml.models.escala;

import com.example.appml.models.musica.Musica;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EscalaDetalhada {

    private int id;
    private String data;
    private String hora;
    private String nome;

    private String baterista;
    private String baixista;
    private String tecladista;
    private String vocalista;  // Singular, conforme JSON
    private String violonista;
    private String guitarrista;
    private String outros;

    @SerializedName("escalas_musicas")
    private List<Musica> musicas;

    private String obs;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    private String url;

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getBaterista() {
        return baterista;
    }

    public void setBaterista(String baterista) {
        this.baterista = baterista;
    }

    public String getBaixista() {
        return baixista;
    }

    public void setBaixista(String baixista) {
        this.baixista = baixista;
    }

    public String getTecladista() {
        return tecladista;
    }

    public void setTecladista(String tecladista) {
        this.tecladista = tecladista;
    }

    public String getVocalista() {
        return vocalista;
    }

    public void setVocalista(String vocalista) {
        this.vocalista = vocalista;
    }

    public String getViolonista() {
        return violonista;
    }

    public void setViolonista(String violonista) {
        this.violonista = violonista;
    }

    public String getGuitarrista() {
        return guitarrista;
    }

    public void setGuitarrista(String guitarrista) {
        this.guitarrista = guitarrista;
    }

    public String getOutros() {
        return outros;
    }

    public void setOutros(String outros) {
        this.outros = outros;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Musica> getMusicas() {
        return musicas;
    }

    // Método auxiliar para verificar se há música associada (exemplo)
    public boolean isTemMusicaAssociada() {
        // Como no JSON não tem campo música, ajuste conforme sua regra
        return false;
    }
}
