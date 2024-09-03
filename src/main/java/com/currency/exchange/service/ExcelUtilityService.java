package com.currency.exchange.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.currency.exchange.configuration.MappingConfig;
import com.currency.exchange.model.ExchangeRate;
import com.currency.exchange.utils.CurrencyExchangeRateConstants;
import com.currency.exchange.utils.CurrencyExchangeRateLogConstants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelUtilityService {

  private final MappingConfig mappingConfig;

  private static final Logger logger = LoggerFactory.getLogger(ExcelUtilityService.class);

  @Autowired
  public ExcelUtilityService(MappingConfig mappingConfig) {
    this.mappingConfig = mappingConfig;
  }

  /**
   * Lee un archivo Excel y convierte cada fila en un objeto ExchangeRate basado en la configuración de mapeo.
   *
   * @param file el archivo Excel a procesar
   * @return una lista de objetos ExchangeRate extraídos del archivo Excel
   */
  public List<ExchangeRate> parseExchangeRates(MultipartFile file) {
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);  // Se asume que sheet nunca será null
      Row headerRow = sheet.getRow(0);

      if (headerRow == null) {
        throw new MultipartException(CurrencyExchangeRateLogConstants.ERROR_INVALID_FILE_FORMAT);
      }

      return IntStream.range(1, sheet.getPhysicalNumberOfRows())
          .mapToObj(sheet::getRow)
          .filter(Objects::nonNull)
          .map(rethrow(row -> mapRowToExchangeRate(row, headerRow)))
          .filter(Objects::nonNull)
          .toList();  // Reemplaza collect(Collectors.toList()) por toList()
    } catch (Exception e) {
      throw new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_INVALID_PROCESS + e.getMessage(), e);
    }
  }

  // Método auxiliar para manejar excepciones checked en expresiones lambda
  private <T, R> Function<T, R> rethrow(FunctionWithException<T, R> function) {
    return arg -> {
      try {
        return function.apply(arg);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  // Interfaz funcional para manejar excepciones checked
  @FunctionalInterface
  interface FunctionWithException<T, R> {

    R apply(T t) throws Exception;
  }

  /**
   * Mapea una fila de Excel a un objeto ExchangeRate dinámicamente usando la configuración de mapeo.
   *
   * @param row la fila actual que se está procesando
   * @param headerRow la fila de encabezado que contiene los nombres de las columnas
   * @return un objeto ExchangeRate con los datos mapeados de la fila de Excel
   */
  private ExchangeRate mapRowToExchangeRate(Row row, Row headerRow) throws IOException {
    ExchangeRate exchangeRate = new ExchangeRate();
    Map<String, String> cellData = new HashMap<>();

    // Leer cada celda en la fila actual usando programación funcional
    IntStream.range(0, row.getPhysicalNumberOfCells())
        .mapToObj(row::getCell)
        .filter(Objects::nonNull)
        .forEach(cell -> {
          String header = headerRow.getCell(cell.getColumnIndex()).toString();
          String fieldName = mappingConfig.getFieldName(header);
          if (fieldName != null) {
            cellData.put(fieldName, getCellValueAsString(cell));
          }
        });

    // Asignar los datos de las celdas a los campos de la entidad ExchangeRate con validaciones
    exchangeRate.setIdExchangeRate(parseInteger(cellData.get(CurrencyExchangeRateConstants.ID_FIELD)));
    exchangeRate.setSourceCurrency(cellData.get(CurrencyExchangeRateConstants.SOURCE_CURRENCY_FIELD));
    exchangeRate.setTargetCurrency(cellData.get(CurrencyExchangeRateConstants.TARGET_CURRENCY_FIELD));

    // Validación y asignación de la tasa de cambio
    Double rate = parseDouble(cellData.get(CurrencyExchangeRateConstants.RATE_FIELD));
    if (rate == null || rate <= 0) {
      throw new IOException("Tasa no válida para el ID " + exchangeRate.getIdExchangeRate());
    }
    exchangeRate.setRate(rate);

    return exchangeRate;
  }

  /**
   * Obtiene el valor de una celda como una cadena de texto.
   *
   * @param cell la celda de la que se va a obtener el valor
   * @return el valor de la celda como String
   */
  private String getCellValueAsString(Cell cell) {
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getDateCellValue().toString();
        } else {
          return String.valueOf(cell.getNumericCellValue());
        }
      case BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      default:
        return ""; // Tratar celdas vacías o desconocidas como cadenas vacías
    }
  }

  /**
   * Convierte una cadena a un Double, devolviendo null en caso de error.
   *
   * @param value la cadena que se va a convertir
   * @return el valor Double o null si hay un error
   */
  private Double parseDouble(String value) {
    try {
      return value != null ? Double.parseDouble(value) : null;
    } catch (NumberFormatException e) {
      logger.info("{}{}", CurrencyExchangeRateLogConstants.ERROR_CONVERT_DOUBLE, value);
      return null;
    }
  }

  private Integer parseInteger(String value) {
    if (value != null && !value.isEmpty()) {
      try {
        // Si el valor es decimal, lo convertimos a entero
        return (int) Double.parseDouble(value);
      } catch (NumberFormatException e) {
        logger.info("{}{}", "Error al convertir el valor a entero: ", e.getMessage());
      }
    }
    return null;
  }
}
