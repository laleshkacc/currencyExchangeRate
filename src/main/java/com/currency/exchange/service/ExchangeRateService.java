package com.currency.exchange.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.currency.exchange.model.ExchangeRate;
import com.currency.exchange.repository.ExchangeRateRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExchangeRateService {

  @Autowired
  private ExchangeRateRepository exchangeRateRepository;

  public List<ExchangeRate> saveExchangeRate(MultipartFile file) throws IOException {
    Workbook workbook = WorkbookFactory.create(file.getInputStream());
    Sheet sheet = workbook.getSheetAt(0);

    Row headerRow = sheet.getRow(0);
    List<ExchangeRate> exchangeRates = new ArrayList<>();

    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row != null) {
        ExchangeRate exchangeRate = new ExchangeRate();
        for (Cell cell : row) {
          String header = headerRow.getCell(cell.getColumnIndex()).toString();
          switch (cell.getCellType()) {
            case STRING:
              String stringValue = cell.getStringCellValue();
              if ("MonedaOrigen".equals(header)) {
                exchangeRate.setSourceCurrency(stringValue);
              } else if ("MonedaDestino".equals(header)) {
                exchangeRate.setTargetCurrency(stringValue);
              }
              break;
            case NUMERIC:
              double numericValue = cell.getNumericCellValue();
              if ("ID".equals(header)) {
                exchangeRate.setIdExchangeRate((int) numericValue);
              } else if ("Tasa".equals(header)) {
                exchangeRate.setRate(numericValue);
              }
              break;
            default:
              break;
          }
        }
        exchangeRateRepository.save(exchangeRate);
      }
    }
    return exchangeRateRepository.saveAll(exchangeRates);
  }

  @GetMapping
  public List<ExchangeRate> getExchangeRate() {
    return exchangeRateRepository.findAll();
  }

  public ExchangeRate updateExchangeRate(Integer id, ExchangeRate newRate) {
    return exchangeRateRepository.findById(id).map(rate -> {
      rate.setSourceCurrency(newRate.getSourceCurrency());
      rate.setTargetCurrency(newRate.getTargetCurrency());
      rate.setRate(newRate.getRate());
      return exchangeRateRepository.save(rate);
    }).orElseThrow(() -> new IllegalArgumentException("Tipo de cambio no encontrado"));
  }

}
