package com.example.appml.models.musica;

public class Musica {
    private int id;
    private String titulo;

    public String getNome() {
        return nome;
    }

    private String nome;

    private String interprete;
    private Versao versao;

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Versao getVersao() { return versao; }
    public void setVersao(Versao versao) { this.versao = versao; }

    // Métodos auxiliares para uso no Adapter:
    public String getLinkYoutube() {
        if (versao != null) return versao.getLinkYoutube();
        return "";
    }

    public String getNomeVersao() {
        if (versao != null) return versao.getNome();
        return "";
    }

    public String getInterprete() {
        return interprete;
    }
}
