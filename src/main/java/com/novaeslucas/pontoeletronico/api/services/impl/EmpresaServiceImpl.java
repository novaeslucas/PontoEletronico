package com.novaeslucas.pontoeletronico.api.services.impl;

import com.novaeslucas.pontoeletronico.api.entities.Empresa;
import com.novaeslucas.pontoeletronico.api.repositories.EmpresaRepository;
import com.novaeslucas.pontoeletronico.api.services.EmpresaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    private static final Logger log = LoggerFactory.getLogger(EmpresaServiceImpl.class);

    @Autowired
    private EmpresaRepository empresaRepository;

    @Override
    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        log.info("Buscando uma empresa para o CNPJ {}", cnpj);
        return Optional.ofNullable(empresaRepository.findByCnpj(cnpj));
    }

    @Override
    public Empresa persistir(Empresa empresa) {
        log.info("Persistindo empresa: {}", empresa);
        return this.empresaRepository.save(empresa);
    }
}