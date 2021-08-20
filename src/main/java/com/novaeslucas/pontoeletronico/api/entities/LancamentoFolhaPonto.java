package com.novaeslucas.pontoeletronico.api.entities;

public class LancamentoFolhaPonto {

    private String data;
    private String turno1Horario1;
    private String turno1Horario2;
    private String turno2Horario1;
    private String turno2Horario2;
    private String turnoExtraHorario1;
    private String turnoExtraHorario2;
    private String horasDia;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTurno1Horario1() {
        return turno1Horario1;
    }

    public void setTurno1Horario1(String turno1Horario1) {
        this.turno1Horario1 = turno1Horario1;
    }

    public String getTurno1Horario2() {
        return turno1Horario2;
    }

    public void setTurno1Horario2(String turno1Horario2) {
        this.turno1Horario2 = turno1Horario2;
    }

    public String getTurno2Horario1() {
        return turno2Horario1;
    }

    public void setTurno2Horario1(String turno2Horario1) {
        this.turno2Horario1 = turno2Horario1;
    }

    public String getTurno2Horario2() {
        return turno2Horario2;
    }

    public void setTurno2Horario2(String turno2Horario2) {
        this.turno2Horario2 = turno2Horario2;
    }

    public String getTurnoExtraHorario1() {
        return turnoExtraHorario1;
    }

    public void setTurnoExtraHorario1(String turnoExtraHorario1) {
        this.turnoExtraHorario1 = turnoExtraHorario1;
    }

    public String getTurnoExtraHorario2() {
        return turnoExtraHorario2;
    }

    public void setTurnoExtraHorario2(String turnoExtraHorario2) {
        this.turnoExtraHorario2 = turnoExtraHorario2;
    }

    public String getHorasDia() {
        return horasDia;
    }

    public void setHorasDia(String horasDia) {
        this.horasDia = horasDia;
    }
}
