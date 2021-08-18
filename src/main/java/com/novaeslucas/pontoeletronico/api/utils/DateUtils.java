package com.novaeslucas.pontoeletronico.api.utils;

import com.novaeslucas.pontoeletronico.api.entities.Lancamento;

import java.util.List;

public interface DateUtils {

    String calcularHorasDia(List<Lancamento> lancamentos);

}
