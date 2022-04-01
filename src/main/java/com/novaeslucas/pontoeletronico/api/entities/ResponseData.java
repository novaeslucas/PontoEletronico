package com.novaeslucas.pontoeletronico.api.entities;

import com.novaeslucas.pontoeletronico.api.dtos.LancamentoDto;

import java.io.Serializable;

public class ResponseData implements Serializable {

    private LancamentoDto data;

    private String errors;

    public LancamentoDto getData() {
        return data;
    }

    public void setData(LancamentoDto data) {
        this.data = data;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

}
