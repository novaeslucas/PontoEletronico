package com.novaeslucas.pontoeletronico.api.controllers;

import java.util.Optional;

import com.novaeslucas.pontoeletronico.api.dtos.EmpresaDto;
import com.novaeslucas.pontoeletronico.api.entities.Empresa;
import com.novaeslucas.pontoeletronico.api.response.Response;
import com.novaeslucas.pontoeletronico.api.services.EmpresaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);

    private EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping(value = "/cnpj/{cnpj}")
    public ResponseEntity<Response<EmpresaDto>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
        log.info("Buscando empresa por CNPJ: {}", cnpj);
        Response<EmpresaDto> response = new Response<>();
        Optional<Empresa> empresa = empresaService.buscarPorCnpj(cnpj);

        if (!empresa.isPresent()) {
            log.info("Empresa não encontrada para o CNPJ: {}", cnpj);
            response.getErrors().add("Empresa não encontrada para o CNPJ " + cnpj);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(this.converterEmpresaDto(empresa.get()));
        return ResponseEntity.ok(response);
    }

    private EmpresaDto converterEmpresaDto(Empresa empresa) {
        EmpresaDto empresaDto = new EmpresaDto();
        empresaDto.setId(empresa.getId());
        empresaDto.setCnpj(empresa.getCnpj());
        empresaDto.setRazaoSocial(empresa.getRazaoSocial());

        return empresaDto;
    }

}
