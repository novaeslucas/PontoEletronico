package com.novaeslucas.pontoeletronico.api.services;

import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest);

    List<Lancamento> buscarPorFuncionarioId(Long funcionarioId);

    Optional<Lancamento> buscarPorId(Long id);

    Lancamento persistir(Lancamento lancamento);

    void remover(Long id);

    List<Lancamento> buscarPorData(Date dataInicial, Date dataFinal);

    List<Lancamento> buscarPorDatasFuncionarioId(Date dataInicial, Date dataFinal, Long id);

    Lancamento buscarPorDataFuncionarioId(Date data, Long funcionarioId);
}
