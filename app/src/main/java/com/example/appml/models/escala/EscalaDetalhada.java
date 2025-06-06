package com.example.appml.models.escala;

import com.google.gson.annotations.SerializedName;

public class EscalaDetalhada {

    private int id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("data")
    private String data;

    @SerializedName("ministerio")
    private String ministerio;

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
    private String vocalistas;

    @SerializedName("violonista")
    private String violonista;

    @SerializedName("guitarrista")
    private String guitarrista;

    @SerializedName("tem_musica_associada")
    private boolean temMusicaAssociada;

    @SerializedName("observacoes")
    private String observacoes;

    // Getters para todos

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getMinisterio() {
        return ministerio;
    }

    public String getHora() {
        return hora;
    }

    public String getBaterista() {
        return baterista;
    }

    public String getBaixista() {
        return baixista;
    }

    public String getSaxofonista() {
        return saxofonista;
    }

    public String getTecladista() {
        return tecladista;
    }

    public String getVocalistas() {
        return vocalistas;
    }

    public String getViolonista() {
        return violonista;
    }

    public String getGuitarrista() {
        return guitarrista;
    }

    public boolean isTemMusicaAssociada() {
        return temMusicaAssociada;
    }

    public String getObservacoes() {
        return observacoes;
    }
}
