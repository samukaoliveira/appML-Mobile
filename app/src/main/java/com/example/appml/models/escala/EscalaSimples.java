package com.example.appml.models.escala;

import com.google.gson.annotations.SerializedName;

public class EscalaSimples {

    private int id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("data")
    private String data;

    @SerializedName("ministerio")
    private String ministerio;

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
}
