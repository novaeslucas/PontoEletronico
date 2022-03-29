package com.novaeslucas.pontoeletronico.api.entities;

import java.time.LocalTime;
import java.util.Date;

public class LancamentosData {

    Long idFuncionario;
    LocalTime inicioTrabalho;
    LocalTime inicioAlmoco;
    LocalTime terminoAlmoco;
    LocalTime terminoTrabalho;
    LocalTime inicioTurnoExtra;
    LocalTime terminoTurnoExtra;
    Date data;
    Date dataAtualizacao;

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public LocalTime getInicioTrabalho() {
        return inicioTrabalho;
    }

    public void setInicioTrabalho(LocalTime inicioTrabalho) {
        this.inicioTrabalho = inicioTrabalho;
    }

    public LocalTime getInicioAlmoco() {
        return inicioAlmoco;
    }

    public void setInicioAlmoco(LocalTime inicioAlmoco) {
        this.inicioAlmoco = inicioAlmoco;
    }

    public LocalTime getTerminoAlmoco() {
        return terminoAlmoco;
    }

    public void setTerminoAlmoco(LocalTime terminoAlmoco) {
        this.terminoAlmoco = terminoAlmoco;
    }

    public LocalTime getTerminoTrabalho() {
        return terminoTrabalho;
    }

    public void setTerminoTrabalho(LocalTime terminoTrabalho) {
        this.terminoTrabalho = terminoTrabalho;
    }

    public LocalTime getInicioTurnoExtra() {
        return inicioTurnoExtra;
    }

    public void setInicioTurnoExtra(LocalTime inicioTurnoExtra) {
        this.inicioTurnoExtra = inicioTurnoExtra;
    }

    public LocalTime getTerminoTurnoExtra() {
        return terminoTurnoExtra;
    }

    public void setTerminoTurnoExtra(LocalTime terminoTurnoExtra) {
        this.terminoTurnoExtra = terminoTurnoExtra;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
