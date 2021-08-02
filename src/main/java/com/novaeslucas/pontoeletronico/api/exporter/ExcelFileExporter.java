package com.novaeslucas.pontoeletronico.api.exporter;

import com.novaeslucas.pontoeletronico.api.entities.Lancamento;
import com.novaeslucas.pontoeletronico.api.enums.TipoCelula;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelFileExporter {

    private static final Logger log = LoggerFactory.getLogger(ExcelFileExporter.class);

    public static ByteArrayInputStream listaLancamentosToExcelFile(List<Lancamento> lancamentos) {
        Workbook workbook = new XSSFWorkbook();
        try{
            Sheet sheet = workbook.createSheet("Lancamentos");
            CellStyle cellStyleData = setEstiloCelula(workbook, TipoCelula.DATA.ordinal());
            CellStyle cellStyleHora = setEstiloCelula(workbook, TipoCelula.HORA.ordinal());

            Row cabecalho = sheet.createRow(0);
            cabecalho.createCell(0).setCellValue("Data");
            cabecalho.createCell(1).setCellValue("Inicio trabalho");
            cabecalho.createCell(2).setCellValue("Inicio almoco");
            cabecalho.createCell(3).setCellValue("Termino almoco");
            cabecalho.createCell(4).setCellValue("Termino trabalho");

            Map<Instant, List<Lancamento>> lancamentosOrdenadosPorData = lancamentos.stream().collect(Collectors.groupingBy(l ->
                    l.getData().toInstant().truncatedTo(ChronoUnit.DAYS)));

            int r = 1;
            for(int i = 0; i < lancamentosOrdenadosPorData.size(); i++){
                int c = 0;
                Instant data = (Instant) lancamentosOrdenadosPorData.keySet().toArray()[i];
                List<Lancamento> lancamentoPorData = lancamentosOrdenadosPorData.get(data);
                Row row = sheet.createRow(r);

                if(lancamentoPorData.size() > 0){
                    Cell dataCelula = row.createCell(c);
                    Instant inst = (Instant) lancamentosOrdenadosPorData.keySet().toArray()[i];
                    OffsetDateTime odt = inst.atOffset(ZoneOffset.UTC);
                    LocalDate ld = odt.toLocalDate();
                    Date d = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());

                    dataCelula.setCellValue(d);
                    dataCelula.setCellStyle(cellStyleData);

                    c++;
                    for (Lancamento lancamento : lancamentoPorData) {
                        Cell horaLancamentoCelula = row.createCell(c);
                        horaLancamentoCelula.setCellValue(lancamento.getData());
                        horaLancamentoCelula.setCellStyle(cellStyleHora);
                        c++;
                    }
                }
                r++;
            }

            sheet.autoSizeColumn(0);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            log.error("Erro: listaLancamentosToExcelFile", ex);
            return null;
        }
    }

    private static CellStyle setEstiloCelula(Workbook wb, int tipoCelula) {
        CellStyle cellStyle = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        switch (TipoCelula.values()[tipoCelula]){
            case MOEDA:
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("R$ #,##0.00;-R$ #,##0.00"));
                break;
            case DATA:
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
                break;
            case DATA_HORA:
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
                break;
            case HORA:
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm"));
                break;
        }
        return cellStyle;
    }
}
