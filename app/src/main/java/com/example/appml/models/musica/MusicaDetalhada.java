package com.example.appml.models.musica;

import java.util.List;

public class MusicaDetalhada {
    private int id;
    private String nome;

    private String interprete;

    private String tomOriginal;

    private String ultimoTomTocado;

    private String bpmOriginal;

    private String ultimoBpmTocado;
    private List<Versao> versoes;

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setTitulo(String titulo) { this.nome = nome; }

    public List<Versao> getVersoes() { return versoes; }

    public String getInterprete() {
        return interprete;
    }


    public String getTomOriginal() {
        return tomOriginal;
    }

    public String getUltimoTomTocado() {
        return ultimoTomTocado;
    }

    public String getBpmOriginal() {
        return bpmOriginal;
    }

    public String getUltimoBpmTocado() {
        return ultimoBpmTocado;
    }
}
