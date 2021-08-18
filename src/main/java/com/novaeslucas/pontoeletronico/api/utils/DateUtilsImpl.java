package com.novaeslucas.pontoeletronico.api.utils;

import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Period;

import java.util.List;

public class DateUtilsImpl implements DateUtils {

    @Override
    public String calcularHorasDia(List<Lancamento> lancamentos) {
        if(lancamentos.size() % 2 == 0){
            int diferenca = 0;
            for (int i = lancamentos.size() - 1; i > 0; i = i - 2){
                DateTime dt1 = new DateTime(lancamentos.get(i).getData().getTime());
                DateTime dt2 = new DateTime(lancamentos.get(i-1).getData().getTime());
                Minutes m = Minutes.minutesBetween(dt2, dt1);
                diferenca = diferenca + m.getMinutes()*60*1000;
            }
            Period p = new Period(diferenca);
            return p.getHours() + ":" + p.getMinutes();
        } else {
            return "Horário em aberto.";
        }
    }
}
