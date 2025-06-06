package com.example.appml.models.escala;

import com.google.gson.annotations.SerializedName;

public class Escala {

    @SerializedName("hora")
    private String hora;

    @SerializedName("baterista")
    private String baterista;

    @SerializedName("baixista")
    private String baixista;

    @SerializedName("saxofonista")
    private String saxofonista;

    @SerializedName("tecladista")
    private String tecladista;

    @SerializedName("vocalistas")
    private String vocalistas; // pode ser uma lista ou string

    @SerializedName("violonista")
    private String violonista;

    @SerializedName("guitarrista")
    private String guitarrista;

    @SerializedName("tem_musica_associada")
    private boolean temMusicaAssociada;

    @SerializedName("obs")
    private String observacoes;

    // getters e setters para todos os campos

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
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

    public String getSaxofonista() {
        return saxofonista;
    }

    public void setSaxofonista(String saxofonista) {
        this.saxofonista = saxofonista;
    }

    public String getTecladista() {
        return tecladista;
    }

    public void setTecladista(String tecladista) {
        this.tecladista = tecladista;
    }

    public String getVocalistas() {
        return vocalistas;
    }

    public void setVocalistas(String vocalistas) {
        this.vocalistas = vocalistas;
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

    public boolean isTemMusicaAssociada() {
        return temMusicaAssociada;
    }

    public void setTemMusicaAssociada(boolean temMusicaAssociada) {
        this.temMusicaAssociada = temMusicaAssociada;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
