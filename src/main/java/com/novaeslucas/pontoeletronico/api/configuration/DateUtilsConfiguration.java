package com.novaeslucas.pontoeletronico.api.configuration;

import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import com.novaeslucas.pontoeletronico.api.utils.DateUtils;
import com.novaeslucas.pontoeletronico.api.utils.DateUtilsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DateUtilsConfiguration {

    @Bean
    public DateUtils dateUtils() {
        return (List<Lancamento> lancamentos) -> new DateUtilsImpl().calcularHorasDia(lancamentos);
    }

}
