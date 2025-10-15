package com.example.appml.models.escala;

import com.google.gson.annotations.SerializedName;

public class EscalaSimples {

    private int id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("data")
    private String data;

    @SerializedName("hora")
    private String hora;

    @SerializedName("funcao")
    private String funcao;

    @SerializedName("ministerio_nome")
    private String ministerio;

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getHora() {
        return hora;
    }

    public String getData() {
        return data;
    }

    public String getMinisterio() {
        return ministerio;
    }

    public String getFuncao() {
        return funcao;
    }

    public boolean semFuncao(){
        if (this.funcao.contains("Não está nesta escala")){
            return true;
        }
        return false;
    }

}
