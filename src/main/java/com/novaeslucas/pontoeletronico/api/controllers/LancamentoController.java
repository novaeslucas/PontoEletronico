package com.novaeslucas.pontoeletronico.api.controllers;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaeslucas.pontoeletronico.api.dtos.LancamentoDto;
import com.novaeslucas.pontoeletronico.api.entities.Funcionario;
import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import com.novaeslucas.pontoeletronico.api.entities.ResponseData;
import com.novaeslucas.pontoeletronico.api.enums.TipoEnum;
import com.novaeslucas.pontoeletronico.api.exporter.ExcelFileExporter;
import com.novaeslucas.pontoeletronico.api.response.Response;
import com.novaeslucas.pontoeletronico.api.services.FuncionarioService;
import com.novaeslucas.pontoeletronico.api.services.LancamentoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.EnumUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

    private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private LancamentoService lancamentoService;

    private FuncionarioService funcionarioService;

    @Value("${paginacao.qtd_por_pagina}")
    private int qtdPorPagina;

    public LancamentoController(LancamentoService lancamentoService, FuncionarioService funcionarioService) {
        this.lancamentoService = lancamentoService;
        this.funcionarioService = funcionarioService;
    }

    @GetMapping(value = "/funcionario/{funcionarioId}")
    public ResponseEntity<Response<Page<LancamentoDto>>> listarPorFuncionarioId(
            @PathVariable("funcionarioId") Long funcionarioId,
            @RequestParam(value = "pag", defaultValue = "0") int pag) {
        log.info("Buscando lançamentos por ID do funcionário: {}, página: {}", funcionarioId, pag);
        Response<Page<LancamentoDto>> response = new Response<>();

        PageRequest pageRequest = PageRequest.of(pag, this.qtdPorPagina, Sort.by("id").descending());
        Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
        Page<LancamentoDto> lancamentosDto = lancamentos.map(lancamento -> this.converterLancamentoDto(lancamento));

        response.setData(lancamentosDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") Long id) {
        log.info("Buscando lançamento por ID: {}", id);
        Response<LancamentoDto> response = new Response<>();
        Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);

        if (!lancamento.isPresent()) {
            log.info("Lançamento não encontrado para o ID: {}", id);
            response.getErrors().add("Lançamento não encontrado para o id " + id);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(this.converterLancamentoDto(lancamento.get()));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<LancamentoDto>> adicionar(@Valid @RequestBody LancamentoDto lancamentoDto,
                                                             BindingResult result) {
        log.info("Adicionando lançamento: {}", lancamentoDto.toString());
        return ResponseEntity.ok(persistir(null, lancamentoDto, result, false));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<LancamentoDto>> atualizar(@PathVariable("id") Long id,
                                                             @Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) {
        log.info("Atualizando lançamento: {}", lancamentoDto.toString());
        return ResponseEntity.ok(persistir(id, lancamentoDto, result, true));
    }

    public Response<LancamentoDto> persistir(Long id, LancamentoDto lancamentoDto, BindingResult result, boolean alterar){
        Response<LancamentoDto> response = new Response<>();
        validarFuncionario(lancamentoDto, result);
        if(alterar){
            lancamentoDto.setId(Optional.of(id));
        }
        Lancamento lancamento = new Lancamento();
        try{
            lancamento = this.converterDtoParaLancamento(lancamentoDto, result);
        } catch (ParseException e){
            log.error("Erro: converterDtoParaLancamento", e);
        }
        if (result.hasErrors()) {
            log.error("Erro validando lançamento: {}", result.getAllErrors());
            result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
            return response;
        }
        lancamento = this.lancamentoService.persistir(lancamento);
        response.setData(this.converterLancamentoDto(lancamento));
        return response;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
        log.info("Removendo lançamento: {}", id);
        Response<String> response = new Response<>();
        Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);

        if (!lancamento.isPresent()) {
            log.info("Erro ao remover devido ao lançamento ID: {} ser inválido.", id);
            response.getErrors().add("Erro ao remover lançamento. Registro não encontrado para o id " + id);
            return ResponseEntity.badRequest().body(response);
        }

        this.lancamentoService.remover(id);
        return ResponseEntity.ok(new Response<>());
    }

    private void validarFuncionario(LancamentoDto lancamentoDto, BindingResult result) {
        if (lancamentoDto.getFuncionarioId() == null) {
            result.addError(new ObjectError("funcionario", "Funcionário não informado."));
            return;
        }

        log.info("Validando funcionário id {}: ", lancamentoDto.getFuncionarioId());
        Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
        if (!funcionario.isPresent()) {
            result.addError(new ObjectError("funcionario", "Funcionário não encontrado. ID inexistente."));
        }
    }

    private LancamentoDto converterLancamentoDto(Lancamento lancamento) {
        LancamentoDto lancamentoDto = new LancamentoDto();
        lancamentoDto.setId(Optional.of(lancamento.getId()));
        lancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
        lancamentoDto.setTipo(lancamento.getTipo().toString());
        lancamentoDto.setDescricao(lancamento.getDescricao());
        lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
        lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());

        return lancamentoDto;
    }

    private Lancamento converterDtoParaLancamento(LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
        Lancamento lancamento = new Lancamento();

        if (lancamentoDto.getId().isPresent()) {
            Optional<Lancamento> lanc = this.lancamentoService.buscarPorId(lancamentoDto.getId().get());
            if (lanc.isPresent()) {
                lancamento = lanc.get();
            } else {
                result.addError(new ObjectError("lancamento", "Lançamento não encontrado."));
            }
        } else {
            lancamento.setFuncionario(new Funcionario());
            lancamento.getFuncionario().setId(lancamentoDto.getFuncionarioId());
        }

        lancamento.setDescricao(lancamentoDto.getDescricao());
        lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
        lancamento.setData(this.dateFormat.parse(lancamentoDto.getData()));

        if (EnumUtils.isValidEnum(TipoEnum.class, lancamentoDto.getTipo())) {
            lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
        } else {
            result.addError(new ObjectError("tipo", "Tipo inválido."));
        }

        return lancamento;
    }

    @ApiOperation(value = "Lança um ponto no sistema de ponto eletrônico", httpMethod = "GET")
    @GetMapping(value = "/qrcode/{id}")
    public ModelAndView lancarPonto(@PathVariable("id") Long id) {
        LancamentoDto lancamentoDto = new LancamentoDto();

        Date dataLancamento = new Date();
        String dataFormatadaDto = this.dateFormat.format(dataLancamento);

        lancamentoDto.setData(dataFormatadaDto);
        lancamentoDto.setDescricao(null);
        lancamentoDto.setLocalizacao(null);
        String tipoLancamento = obterTipoLancamento(lancamentoDto.getData()).toString();
        lancamentoDto.setTipo(tipoLancamento);
        lancamentoDto.setFuncionarioId(id);
        lancamentoDto.setId(null);

        StringBuilder response;
        ObjectMapper mapper = new ObjectMapper();
        LinkedHashMap responseData = null;
        try {

            String jsonInputString = mapper.writeValueAsString(lancamentoDto);

            URL url = new URL("http://localhost:8080/api/lancamentos");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            OutputStream os = conn.getOutputStream();
            os.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
            os.close();

            try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))){
                response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            conn.disconnect();

            responseData = mapper.readValue(response.toString(), LinkedHashMap.class);
        } catch (IOException e){
            log.error("Erro: adicionarViaQrCode", e);
        }

        ModelAndView mv = null;

        if(responseData != null){
            if(responseData.get("data") != null){
                mv = new ModelAndView("lancamento_executado");
                mv.addObject("dataPonto", formatarDataPagina(dataLancamento));
            }else{
                mv = new ModelAndView("error");
            }

        }

        return mv;
    }

    private TipoEnum obterTipoLancamento(String dataDto) {
        Date dataInicial = alterarTempoData(dataDto, 0, 0);
        Date dataFinal = alterarTempoData(dataDto, 23, 59);

        List<Lancamento> lancamentos = this.lancamentoService.buscarPorData(dataInicial, dataFinal);
        TipoEnum tipoLancamento;
        switch (lancamentos.size()){
            case 0:
                tipoLancamento = TipoEnum.INICIO_TRABALHO;
                break;
            case 1:
                tipoLancamento = TipoEnum.INICIO_ALMOCO;
                break;
            case 2:
                tipoLancamento = TipoEnum.TERMINO_ALMOCO;
                break;
            case 3:
                tipoLancamento = TipoEnum.TERMINO_TRABALHO;
                break;
            case 4:
                tipoLancamento = TipoEnum.INICIO_TURNO_EXTRA;
                break;
            case 5:
                tipoLancamento = TipoEnum.TERMINO_TURNO_EXTRA;
                break;
            default:
                throw new IllegalStateException("Quantidade de lancamentos excedidos: " + lancamentos.size());
        }

        return tipoLancamento;
    }

    private Date alterarTempoData(String dataDto, int hora, int minuto){
        Date data = new Date();
        try{
            data = this.dateFormat.parse(dataDto);
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR_OF_DAY, hora);
            cal.set(Calendar.MINUTE, minuto);
            data = cal.getTime();
        } catch (ParseException e) {
            log.error("Erro: alterarTempoData: erro no Parse", e);
        }
        return data;
    }

    private Date alterarDiaHoraData(String dataDto, boolean primeiroDia){
        Date data = new Date();
        try{
            data = this.dateFormat.parse(dataDto);
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            if(primeiroDia){
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
            }else{
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
            }
            data = cal.getTime();
        } catch (ParseException e) {
            log.error("Erro: alterarDiaHoraData: erro no Parse", e);
        }
        return data;
    }

    private String formatarDataPagina(Date dataLancamento){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(dataLancamento);
    }

    @GetMapping("/download/{id}/{data}")
    public ResponseEntity<Object> downloadRelatorioMensal(@PathVariable("id") Long id, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date data, HttpServletResponse response) throws IOException {
        Date dataInicialMes = this.alterarDiaHoraData(this.dateFormat.format(data), true);
        Date dataFinalMes = this.alterarDiaHoraData(this.dateFormat.format(data), false);
        List<Lancamento> lancamentosMes = this.lancamentoService.buscarPorDataFuncionarioId(dataInicialMes, dataFinalMes, id);
        if(lancamentosMes.size() > 0){
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=lancamentos.xlsx");
            ByteArrayInputStream stream = ExcelFileExporter.listaLancamentosToExcelFile(lancamentosMes);
            IOUtils.copy(stream, response.getOutputStream());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
