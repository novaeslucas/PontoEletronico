package com.novaeslucas.pontoeletronico.api.entities;

import java.util.Date;

public class LandamentosData {

    Long idFuncionario;
    Date inicioTrabalho;
    Date inicioAlmoco;
    Date terminoAlmoco;
    Date terminoTrabalho;
    Date dataAtualizacao;

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public Date getInicioTrabalho() {
        return inicioTrabalho;
    }

    public void setInicioTrabalho(Date inicioTrabalho) {
        this.inicioTrabalho = inicioTrabalho;
    }

    public Date getInicioAlmoco() {
        return inicioAlmoco;
    }

    public void setInicioAlmoco(Date inicioAlmoco) {
        this.inicioAlmoco = inicioAlmoco;
    }

    public Date getTerminoAlmoco() {
        return terminoAlmoco;
    }

    public void setTerminoAlmoco(Date terminoAlmoco) {
        this.terminoAlmoco = terminoAlmoco;
    }

    public Date getTerminoTrabalho() {
        return terminoTrabalho;
    }

    public void setTerminoTrabalho(Date terminoTrabalho) {
        this.terminoTrabalho = terminoTrabalho;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
