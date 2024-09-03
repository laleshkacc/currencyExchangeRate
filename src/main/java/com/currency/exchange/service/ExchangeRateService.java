package com.currency.exchange.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.currency.exchange.model.ExchangeRate;
import com.currency.exchange.repository.ExchangeRateRepository;
import com.currency.exchange.utils.CurrencyExchangeRateConstants;
import com.currency.exchange.utils.CurrencyExchangeRateLogConstants;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExchangeRateService {

  private final ExchangeRateRepository exchangeRateRepository;

  private final ExcelUtilityService excelUtilityService;

  private final FileStorageService fileStorageService;

  @Autowired
  public ExchangeRateService(ExchangeRateRepository exchangeRateRepository, ExcelUtilityService excelUtilityService,
      FileStorageService fileStorageService) {
    this.exchangeRateRepository = exchangeRateRepository;
    this.excelUtilityService = excelUtilityService;
    this.fileStorageService = fileStorageService;
  }

  /**
   * Procesa un archivo Excel y guarda los tipos de cambio en la base de datos.
   *
   * @param file el archivo Excel a procesar
   * @return la lista de tipos de cambio guardados
   */
  public List<ExchangeRate> saveExchangeRates(MultipartFile file) {

    // Validar que el archivo no sea null y tenga la extensión correcta
    if (file == null || file.isEmpty() || !file.getOriginalFilename().endsWith(".xlsx")) {
      throw new IllegalArgumentException("El archivo debe ser un archivo Excel con extensión .xlsx");
    }

    // Guardar el archivo Excel y sus metadatos
    fileStorageService.storeFile(file);

    try {
      // Procesar el archivo Excel
      List<ExchangeRate> exchangeRates = excelUtilityService.parseExchangeRates(file);

      // Validar y guardar los tipos de cambio usando lotes y programación funcional
      return IntStream.range(0, exchangeRates.size())
          .filter(i -> i % CurrencyExchangeRateConstants.BATCH_SIZE == 0)
          .mapToObj(i -> exchangeRates.subList(i, Math.min(i + CurrencyExchangeRateConstants.BATCH_SIZE, exchangeRates.size())))
          .flatMap(batch -> exchangeRateRepository.saveAll(batch.stream()
              .filter(this::isValidExchangeRate) // Validar cada tipo de cambio
              .collect(Collectors.toList())).stream())
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_SAVE_TYPE_CHANGE + e.getMessage());
    }
  }

  /**
   * Obtiene todos los tipos de cambio de la base de datos.
   *
   * @return la lista de todos los tipos de cambio
   */
  public List<ExchangeRate> getExchangeRates() {
    return exchangeRateRepository.findAll();
  }

  /**
   * Actualiza un tipo de cambio existente en la base de datos.
   *
   * @param id el ID del tipo de cambio a actualizar
   * @param newRate los nuevos datos del tipo de cambio
   * @return el tipo de cambio actualizado
   */
  @Transactional
  public ExchangeRate updateExchangeRate(Integer id, ExchangeRate newRate) {
    return exchangeRateRepository.findById(id).map(rate -> {
      rate.setSourceCurrency(newRate.getSourceCurrency());
      rate.setTargetCurrency(newRate.getTargetCurrency());
      rate.setRate(newRate.getRate());
      validateExchangeRate(rate);
      return exchangeRateRepository.save(rate);
    }).orElseThrow(() -> new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_EXCHANGE_RATE_NOT_FOUND));
  }

  /**
   * Valida un objeto ExchangeRate para asegurar que todos los campos requeridos sean válidos.
   *
   * @param exchangeRate el tipo de cambio a validar
   * @return true si el tipo de cambio es válido, false de lo contrario
   */
  private boolean isValidExchangeRate(ExchangeRate exchangeRate) {
    return Optional.ofNullable(exchangeRate).map(
        rate -> isNonEmpty(String.valueOf(rate.getIdExchangeRate())) && isNonEmpty(rate.getSourceCurrency()) && isNonEmpty(
            rate.getTargetCurrency()) && isPositive(rate.getRate())).orElse(false);
  }

  /**
   * Verifica si el valor es no nulo y no está vacío.
   *
   * @param value el valor a verificar
   * @return true si el valor es no nulo y no está vacío, false en caso contrario
   */
  private boolean isNonEmpty(String value) {
    return value != null && !value.trim().isEmpty();
  }

  /**
   * Verifica si el valor es positivo.
   *
   * @param value el valor a verificar
   * @return true si el valor es positivo, false en caso contrario
   */
  private boolean isPositive(Double value) {
    return value != null && value > 0;
  }

  /**
   * Lanza una excepción si el objeto ExchangeRate no es válido.
   *
   * @param exchangeRate el tipo de cambio a validar
   */
  private void validateExchangeRate(ExchangeRate exchangeRate) {
    if (!isValidExchangeRate(exchangeRate)) {
      throw new IllegalArgumentException(CurrencyExchangeRateLogConstants.ERROR_INVALID_FILE_FORMAT);
    }
  }
}
