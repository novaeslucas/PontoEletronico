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
    String inicioTrabalhoAntigo;
    String inicioAlmocoAntigo;
    String terminoAlmocoAntigo;
    String terminoTrabalhoAntigo;
    String inicioTurnoExtraAntigo;
    String terminoTurnoExtraAntigo;
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

    public String getInicioTrabalhoAntigo() {
        return inicioTrabalhoAntigo;
    }

    public void setInicioTrabalhoAntigo(String inicioTrabalhoAntigo) {
        this.inicioTrabalhoAntigo = inicioTrabalhoAntigo;
    }

    public String getInicioAlmocoAntigo() {
        return inicioAlmocoAntigo;
    }

    public void setInicioAlmocoAntigo(String inicioAlmocoAntigo) {
        this.inicioAlmocoAntigo = inicioAlmocoAntigo;
    }

    public String getTerminoAlmocoAntigo() {
        return terminoAlmocoAntigo;
    }

    public void setTerminoAlmocoAntigo(String terminoAlmocoAntigo) {
        this.terminoAlmocoAntigo = terminoAlmocoAntigo;
    }

    public String getTerminoTrabalhoAntigo() {
        return terminoTrabalhoAntigo;
    }

    public void setTerminoTrabalhoAntigo(String terminoTrabalhoAntigo) {
        this.terminoTrabalhoAntigo = terminoTrabalhoAntigo;
    }

    public String getInicioTurnoExtraAntigo() {
        return inicioTurnoExtraAntigo;
    }

    public void setInicioTurnoExtraAntigo(String inicioTurnoExtraAntigo) {
        this.inicioTurnoExtraAntigo = inicioTurnoExtraAntigo;
    }

    public String getTerminoTurnoExtraAntigo() {
        return terminoTurnoExtraAntigo;
    }

    public void setTerminoTurnoExtraAntigo(String terminoTurnoExtraAntigo) {
        this.terminoTurnoExtraAntigo = terminoTurnoExtraAntigo;
    }
}
