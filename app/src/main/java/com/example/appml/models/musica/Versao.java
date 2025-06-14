package com.example.appml.models.musica;

import com.google.gson.annotations.SerializedName;

public class Versao {
    private int id;
    private String nome;

    @SerializedName("link_youtube")
    private String linkYoutube;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLinkYoutube() { return linkYoutube; }
    public void setLinkYoutube(String linkYoutube) { this.linkYoutube = linkYoutube; }
}
