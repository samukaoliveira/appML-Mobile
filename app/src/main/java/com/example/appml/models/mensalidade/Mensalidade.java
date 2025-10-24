package com.example.appml.models.mensalidade;

import com.google.gson.annotations.SerializedName;

public class Mensalidade {

    private int id;
    private String mes;
    private int ano;

    @SerializedName("membro_id")
    private int membroId;

    private String status;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    private Double valor;

    @SerializedName("chave_pix")
    private String chavePix;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public int getMembroId() { return membroId; }
    public void setMembroId(int membroId) { this.membroId = membroId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }

    // Conveniência para o adapter
    public String getPix() {
        return chavePix;
    }
}
