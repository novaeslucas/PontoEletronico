package com.novaeslucas.pontoeletronico.api.dtos;

import java.time.LocalTime;

public class LancamentosDataDto {

    Long id;
    String inicioTrabalho;
    String inicioAlmoco;
    String terminoAlmoco;
    String terminoTrabalho;
    String inicioTurnoExtra;
    String terminoTurnoExtra;
    String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInicioTrabalho() {
        return inicioTrabalho;
    }

    public void setInicioTrabalho(String inicioTrabalho) {
        this.inicioTrabalho = inicioTrabalho;
    }

    public String getInicioAlmoco() {
        return inicioAlmoco;
    }

    public void setInicioAlmoco(String inicioAlmoco) {
        this.inicioAlmoco = inicioAlmoco;
    }

    public String getTerminoAlmoco() {
        return terminoAlmoco;
    }

    public void setTerminoAlmoco(String terminoAlmoco) {
        this.terminoAlmoco = terminoAlmoco;
    }

    public String getTerminoTrabalho() {
        return terminoTrabalho;
    }

    public void setTerminoTrabalho(String terminoTrabalho) {
        this.terminoTrabalho = terminoTrabalho;
    }

    public String getInicioTurnoExtra() {
        return inicioTurnoExtra;
    }

    public void setInicioTurnoExtra(String inicioTurnoExtra) {
        this.inicioTurnoExtra = inicioTurnoExtra;
    }

    public String getTerminoTurnoExtra() {
        return terminoTurnoExtra;
    }

    public void setTerminoTurnoExtra(String terminoTurnoExtra) {
        this.terminoTurnoExtra = terminoTurnoExtra;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
