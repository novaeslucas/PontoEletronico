package com.novaeslucas.pontoeletronico.api.utils;

import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.List;

public class DateUtils {

    public static String calcularHorasDia(List<Lancamento> lancamentos) {
        if(lancamentos != null && lancamentos.size() % 2 == 0){
            int diferenca = 0;
            for (int i = lancamentos.size() - 1; i > 0; i = i - 2){
                DateTime dt1 = new DateTime(lancamentos.get(i).getData().getTime());
                DateTime dt2 = new DateTime(lancamentos.get(i-1).getData().getTime());
                Minutes m = Minutes.minutesBetween(dt2, dt1);
                diferenca = diferenca + m.getMinutes()*60*1000;
            }
            PeriodFormatter pf = new PeriodFormatterBuilder().appendHours().appendSuffix("h ").appendMinutes().appendSuffix("m").toFormatter();
            return pf.print(new Period(diferenca));
        } else {
            return "-";
        }
    }
}
