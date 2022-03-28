package com.novaeslucas.pontoeletronico.api.services.impl;

import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import com.novaeslucas.pontoeletronico.api.repositories.LancamentoRepository;
import com.novaeslucas.pontoeletronico.api.services.LancamentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);

    private LancamentoRepository lancamentoRepository;

    public LancamentoServiceImpl(LancamentoRepository lancamentoRepository){
        this.lancamentoRepository = lancamentoRepository;
    }

    public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
        log.info("Buscando lançamentos para o funcionário ID {}", funcionarioId);
        return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
    }

    public List<Lancamento> buscarPorFuncionarioId(Long funcionarioId) {
        log.info("Buscando lançamentos para o funcionário ID {}", funcionarioId);
        return this.lancamentoRepository.findByFuncionarioId(funcionarioId);
    }

    @Cacheable("lancamentoPorId")
    public Optional<Lancamento> buscarPorId(Long id) {
        log.info("Buscando um lançamento pelo ID {}", id);
        return this.lancamentoRepository.findById(id);
    }

    @CachePut("lancamentoPorId")
    public Lancamento persistir(Lancamento lancamento) {
        log.info("Persistindo o lançamento: {}", lancamento);
        return this.lancamentoRepository.save(lancamento);
    }

    public void remover(Long id) {
        log.info("Removendo o lançamento ID {}", id);
        Lancamento lancamento = this.lancamentoRepository.findById(id).get();
        this.lancamentoRepository.delete(lancamento);
    }

    public List<Lancamento> buscarPorData(Date dataInicial, Date dataFinal) {
        log.info("Buscando um lançamento pela data {}", dataInicial, dataFinal);
        return this.lancamentoRepository.findByData(dataInicial, dataFinal);
    }

    public List<Lancamento> buscarPorDatasFuncionarioId(Date dataInicial, Date dataFinal, Long funcionarioId) {
        log.info("Buscando um lançamento pela data {}", dataInicial, dataFinal, funcionarioId);
        return this.lancamentoRepository.findByDatasFuncionarioId(dataInicial, dataFinal, funcionarioId);
    }

    public List<Lancamento> buscarPorDataFuncionarioId(Date data, Long funcionarioId) {
        log.info("Buscando lançamentos pela data e pelo funcionario{}", data, funcionarioId);
        return this.lancamentoRepository.findByDataFuncionarioId(data, funcionarioId);
    }

}
