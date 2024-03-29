package com.novaeslucas.pontoeletronico.api.controllers;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaeslucas.pontoeletronico.api.dtos.LancamentoDto;
import com.novaeslucas.pontoeletronico.api.dtos.LancamentosDataDto;
import com.novaeslucas.pontoeletronico.api.entities.Funcionario;
import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import com.novaeslucas.pontoeletronico.api.entities.LancamentoFolhaPonto;
import com.novaeslucas.pontoeletronico.api.entities.LancamentosData;
import com.novaeslucas.pontoeletronico.api.enums.TipoEnum;
import com.novaeslucas.pontoeletronico.api.exporter.ExcelFileExporter;
import com.novaeslucas.pontoeletronico.api.response.Response;
import com.novaeslucas.pontoeletronico.api.services.FuncionarioService;
import com.novaeslucas.pontoeletronico.api.services.LancamentoService;
import com.novaeslucas.pontoeletronico.api.utils.DateUtils;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

    private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
    private LancamentoService lancamentoService;
    private FuncionarioService funcionarioService;
    @Value("${paginacao.qtd_por_pagina}")
    private int qtdPorPagina;
    private static final String ESPACO = " ";
    private static final String VAZIO = "";
    private static final String HORA_ZERO = "00:00:00";
    private static final String SEGUNDOS_ZERADO = ":00";

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
        Page<LancamentoDto> lancamentosDto = lancamentos.map(this::converterLancamentoDto);

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
        lancamentoDto.setData(retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lancamento.getData()));
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
        lancamento.setData(retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lancamentoDto.getData()));

        if (EnumUtils.isValidEnum(TipoEnum.class, lancamentoDto.getTipo())) {
            lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
        } else {
            result.addError(new ObjectError("tipo", "Tipo inválido."));
        }

        return lancamento;
    }

    @ApiOperation(value = "Lança um ponto no sistema de ponto eletrônico", httpMethod = "GET")
    @GetMapping(value = "/qrcode/{id}")
    public ModelAndView preLancarPonto(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("lancar_ponto");
        mv.addObject("id", id);
        return mv;
    }

    @PostMapping(value = "/lancar-ponto/{id}")
    public ModelAndView lancarPonto(@PathVariable("id") Long id) {
        LancamentoDto lancamentoDto = new LancamentoDto();

        Date dataLancamento = new Date();
        String dataFormatadaDto = retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dataLancamento);

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
            data = retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataDto);
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
            data = retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataDto);
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

    private Date alterarDiaHoraData(Date data, boolean primeiroDia){
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        if(primeiroDia){
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
        }else{
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
        }
        data = cal.getTime();
        return data;
    }

    private String formatarDataPagina(Date dataLancamento){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(dataLancamento);
    }

    @GetMapping("/download/{id}/{data}")
    public ResponseEntity<Object> downloadRelatorioMensal(@PathVariable("id") Long id, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date data, HttpServletResponse response) throws IOException {
        Date dataInicialMes = this.alterarDiaHoraData(retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data), true);
        Date dataFinalMes = this.alterarDiaHoraData(retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data), false);
        List<Lancamento> lancamentosMes = this.lancamentoService.buscarPorDatasFuncionarioId(dataInicialMes, dataFinalMes, id);
        if(lancamentosMes.size() > 0){
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=lancamentos.xlsx");
            ByteArrayInputStream stream = ExcelFileExporter.listaLancamentosToExcelFile(lancamentosMes);
            assert stream != null;
            IOUtils.copy(stream, response.getOutputStream());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public SimpleDateFormat retornarArgumentoSimpleDateFormat(String padrao){
        return new SimpleDateFormat(padrao);
    }

    @GetMapping({"/folha-ponto/{id}", "/folha-ponto/{id}/{data}"})
    public ModelAndView folhaPonto(@PathVariable("id") Long id, @PathVariable(name = "data", required = false) Optional<String> dataReferencia){
        Date d = new Date();
        if(dataReferencia.isPresent()){
            try {
                d = retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataReferencia.get() + " 00:00:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Date dataInicialMes = this.alterarDiaHoraData(retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d), true);
        Date dataFinalMes = this.alterarDiaHoraData(retornarArgumentoSimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d), false);
        List<Lancamento> lancamentosMes = this.lancamentoService.buscarPorDatasFuncionarioId(dataInicialMes, dataFinalMes, id);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dataFinalMes);
        int ultimoDiaMes = cal.getActualMaximum(Calendar.DATE);
        List<String> paramDataList = new ArrayList<>();

        ModelAndView mv;
        if(lancamentosMes.size() > 0){
            Map<String, List<Lancamento>> map = lancamentosMes.stream().collect(Collectors.groupingBy(l -> retornarArgumentoSimpleDateFormat("dd/MM/yyyy").format(l.getData())));
            String mesAno = retornarArgumentoSimpleDateFormat("/MM/yyyy").format(d);
            for(int i = 0; i < ultimoDiaMes; i++){
                String diaDoMes = i >= 0 && i < 9 ? "0" + (i + 1) + mesAno : (i + 1) + mesAno;
                if(!map.containsKey(diaDoMes)){
                    map.put(diaDoMes, null);
                }
            }

            Map<String, LancamentoFolhaPonto> mapAlterado = new TreeMap<>();
            for (Map.Entry<String, List<Lancamento>> entry : map.entrySet()){
                LancamentoFolhaPonto lfp = new LancamentoFolhaPonto();
                lfp.setData(entry.getKey());
                if(entry.getValue() == null){
                    mapAlterado.put(entry.getKey(), lfp);
                }else{
                    for(Lancamento l : entry.getValue()){
                        switch (l.getTipo()){
                            case INICIO_TRABALHO:
                                lfp.setTurno1Horario1(retornarArgumentoSimpleDateFormat("HH:mm").format(l.getData()));
                                break;
                            case INICIO_ALMOCO:
                                lfp.setTurno1Horario2(retornarArgumentoSimpleDateFormat("HH:mm").format(l.getData()));
                                break;
                            case TERMINO_ALMOCO:
                                lfp.setTurno2Horario1(retornarArgumentoSimpleDateFormat("HH:mm").format(l.getData()));
                                break;
                            case TERMINO_TRABALHO:
                                lfp.setTurno2Horario2(retornarArgumentoSimpleDateFormat("HH:mm").format(l.getData()));
                                break;
                            case INICIO_TURNO_EXTRA:
                                lfp.setTurnoExtraHorario1(retornarArgumentoSimpleDateFormat("HH:mm").format(l.getData()));
                                break;
                            case TERMINO_TURNO_EXTRA:
                                lfp.setTurnoExtraHorario2(retornarArgumentoSimpleDateFormat("HH:mm").format(l.getData()));
                                break;
                        }
                    }
                    lfp.setHorasDia(DateUtils.calcularHorasDia(entry.getValue()));
                    mapAlterado.put(entry.getKey(), lfp);
                }
            }

            for (String data: mapAlterado.keySet()) {
                Date paramDate = null;
                try {
                    paramDate = retornarArgumentoSimpleDateFormat("dd/MM/yyyy").parse(data);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                paramDataList.add(retornarArgumentoSimpleDateFormat("yyyy-MM-dd").format(paramDate));
            }

            mv = new ModelAndView("folha_ponto");
            mv.addObject("map", mapAlterado);
            mv.addObject("paramFuncionarioId", id);
            mv.addObject("paramDataList", paramDataList);
            mv.addObject("paramDataDownload", retornarArgumentoSimpleDateFormat("yyyy-MM-dd").format(dataInicialMes));
        }else{
            mv = new ModelAndView("error");
        }
        return mv;
    }

    @GetMapping(value = "/lancamentos-data/{funcionarioId}/{data}")
    public ModelAndView lancamentosDataForm(@PathVariable("funcionarioId") Long funcionarioId, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date data) {
        Date dataInicial = this.alterarDiaHoraData(data, true);
        Date dataFinal = this.alterarDiaHoraData(data, false);
        List<Lancamento> lancamentos = this.lancamentoService.buscarPorDatasFuncionarioId(dataInicial, dataFinal, funcionarioId);
        LancamentosData lancamentosData = new LancamentosData();
        lancamentosData.setIdFuncionario(funcionarioId);
        lancamentosData.setDataAtualizacao(new Date());
        lancamentosData.setData(data);
        for (Lancamento l: lancamentos) {
            Date input = l.getData();
            Instant instant = input.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            LocalTime hora = zdt.toLocalTime();
            switch (l.getTipo()){
                case INICIO_TRABALHO:
                    lancamentosData.setInicioTrabalho(hora);
                    break;
                case INICIO_ALMOCO:
                    lancamentosData.setInicioAlmoco(hora);
                    break;
                case TERMINO_ALMOCO:
                    lancamentosData.setTerminoAlmoco(hora);
                    break;
                case TERMINO_TRABALHO:
                    lancamentosData.setTerminoTrabalho(hora);
                    break;
                case INICIO_TURNO_EXTRA:
                    lancamentosData.setInicioTurnoExtra(hora);
                    break;
                case TERMINO_TURNO_EXTRA:
                    lancamentosData.setTerminoTurnoExtra(hora);
                    break;
            }
        }
        ModelAndView mv = new ModelAndView("lancamentos_data");
        mv.addObject("lancamentosData", lancamentosData);
        return mv;
    }

    @PostMapping(value = "/lancamentos-data/{funcionarioId}/{data}")
    public ModelAndView lancamentosDataEdit(@PathVariable("funcionarioId") Long funcionarioId, @Valid @ModelAttribute("lancamentosDataDto") LancamentosDataDto lancamentosDataDto, BindingResult result){
        if(lancamentosDataDto.getInicioTrabalho() != null && !lancamentosDataDto.getInicioTrabalho().equals(lancamentosDataDto.getInicioTrabalhoAntigo())){
            String lancamentoAntigo = lancamentosDataDto.getInicioTrabalhoAntigo().equals(VAZIO) ? lancamentosDataDto.getData() + ESPACO + HORA_ZERO : lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getInicioTrabalhoAntigo();
            persistirLancamentosData(funcionarioId, lancamentoAntigo, lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getInicioTrabalho(), TipoEnum.INICIO_TRABALHO.toString(), result);
        }
        if(lancamentosDataDto.getInicioAlmoco() != null && !lancamentosDataDto.getInicioAlmoco().equals(lancamentosDataDto.getInicioAlmocoAntigo())){
            String lancamentoAntigo = lancamentosDataDto.getInicioAlmocoAntigo().equals(VAZIO) ? lancamentosDataDto.getData() + ESPACO + HORA_ZERO : lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getInicioAlmocoAntigo();
            persistirLancamentosData(funcionarioId, lancamentoAntigo, lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getInicioAlmoco(), TipoEnum.INICIO_ALMOCO.toString(), result);
        }
        if(lancamentosDataDto.getTerminoAlmoco() != null && !lancamentosDataDto.getTerminoAlmoco().equals(lancamentosDataDto.getTerminoAlmocoAntigo())){
            String lancamentoAntigo = lancamentosDataDto.getTerminoAlmocoAntigo().equals(VAZIO) ? lancamentosDataDto.getData() + ESPACO + HORA_ZERO : lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getTerminoAlmocoAntigo();
            persistirLancamentosData(funcionarioId, lancamentoAntigo, lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getTerminoAlmoco(), TipoEnum.TERMINO_ALMOCO.toString(), result);
        }
        if(lancamentosDataDto.getTerminoTrabalho() != null && !lancamentosDataDto.getTerminoTrabalho().equals(lancamentosDataDto.getTerminoTrabalhoAntigo())){
            String lancamentoAntigo = lancamentosDataDto.getTerminoTrabalhoAntigo().equals(VAZIO) ? lancamentosDataDto.getData() + ESPACO + HORA_ZERO : lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getTerminoTrabalhoAntigo();
            persistirLancamentosData(funcionarioId, lancamentoAntigo, lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getTerminoTrabalho(), TipoEnum.TERMINO_TRABALHO.toString(), result);
        }
        if(lancamentosDataDto.getInicioTurnoExtra() != null && !lancamentosDataDto.getInicioTurnoExtra().equals(lancamentosDataDto.getInicioTurnoExtraAntigo())){
            String lancamentoAntigo = lancamentosDataDto.getInicioTurnoExtraAntigo().equals(VAZIO) ? lancamentosDataDto.getData() + ESPACO + HORA_ZERO : lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getInicioTurnoExtraAntigo();
            persistirLancamentosData(funcionarioId, lancamentoAntigo, lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getInicioTurnoExtra(), TipoEnum.INICIO_TURNO_EXTRA.toString(), result);
        }
        if(lancamentosDataDto.getTerminoTurnoExtra() != null && !lancamentosDataDto.getTerminoTurnoExtra().equals(lancamentosDataDto.getTerminoTurnoExtraAntigo())){
            String lancamentoAntigo = lancamentosDataDto.getTerminoTurnoExtraAntigo().equals(VAZIO) ? lancamentosDataDto.getData() + ESPACO + HORA_ZERO : lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getTerminoTurnoExtraAntigo();
            persistirLancamentosData(funcionarioId, lancamentoAntigo, lancamentosDataDto.getData() + ESPACO + lancamentosDataDto.getTerminoTurnoExtra(), TipoEnum.TERMINO_TURNO_EXTRA.toString(), result);
        }

        //verificar se erros no result e retornar para pagina de erro se erro
        return new ModelAndView("lancamento_editado");
    }

    private void persistirLancamentosData(Long funcionarioId, String lancamentoAntigo, String novaDataCompleta, String tipoLancamento, BindingResult result) {
        Date dataAntigaConvertida = null;
        try {
            dataAntigaConvertida = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(lancamentoAntigo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Lancamento l = this.lancamentoService.buscarPorDataFuncionarioId(dataAntigaConvertida, funcionarioId);
        if(l != null){
            LancamentoDto editLancamento = new LancamentoDto();
            editLancamento.setData(novaDataCompleta);
            editLancamento.setFuncionarioId(l.getFuncionario().getId());
            editLancamento.setTipo(l.getTipo().toString());
            this.persistir(l.getId(), editLancamento, result, true);
        }else {
            LancamentoDto novoLancamento = new LancamentoDto();
            novoLancamento.setData(novaDataCompleta);
            novoLancamento.setFuncionarioId(funcionarioId);
            novoLancamento.setTipo(tipoLancamento);
            this.persistir(null, novoLancamento, result, false);
        }
    }

}