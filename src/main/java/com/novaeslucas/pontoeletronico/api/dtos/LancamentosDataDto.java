package com.novaeslucas.pontoeletronico.api.dtos;

public class LancamentosDataDto {

    String inicioTrabalho;
    String inicioAlmoco;
    String terminoAlmoco;
    String terminoTrabalho;
    String inicioHoraExtra;
    String terminoHoraExtra;

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

    public String getInicioHoraExtra() {
        return inicioHoraExtra;
    }

    public void setInicioHoraExtra(String inicioHoraExtra) {
        this.inicioHoraExtra = inicioHoraExtra;
    }

    public String getTerminoHoraExtra() {
        return terminoHoraExtra;
    }

    public void setTerminoHoraExtra(String terminoHoraExtra) {
        this.terminoHoraExtra = terminoHoraExtra;
    }
}
