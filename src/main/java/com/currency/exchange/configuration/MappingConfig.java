package com.currency.exchange.configuration;

import java.util.HashMap;
import java.util.Map;

import com.currency.exchange.utils.CurrencyExchangeRateConstants;
import org.springframework.stereotype.Component;

@Component
public class MappingConfig {

  private final Map<String, String> columnFieldMap;

  public MappingConfig() {
    // Inicializar el mapa de columnas a campos utilizando programación funcional
    columnFieldMap = new HashMap<>();
    initColumnFieldMap();
  }

  /**
   * Método privado para inicializar el mapa de mapeo de columna a campo.
   */
  private void initColumnFieldMap() {
    // Usar una expresión lambda para poblar el mapa con mapeos de columnas a campos
    Map<String, String> initialMappings =
        Map.of(CurrencyExchangeRateConstants.ID, CurrencyExchangeRateConstants.ID_FIELD, CurrencyExchangeRateConstants.SOURCE_CURRENCY,
            CurrencyExchangeRateConstants.SOURCE_CURRENCY_FIELD, CurrencyExchangeRateConstants.TARGET_CURRENCY,
            CurrencyExchangeRateConstants.TARGET_CURRENCY_FIELD, CurrencyExchangeRateConstants.RATE,
            CurrencyExchangeRateConstants.RATE_FIELD);

    initialMappings.forEach((key, value) -> columnFieldMap.put(key, value));
  }

  /**
   * Obtiene el nombre del campo correspondiente para un encabezado de columna.
   *
   * @param columnHeader el nombre del encabezado de columna en el Excel
   * @return el nombre del campo de la entidad correspondiente o null si no existe
   */
  public String getFieldName(String columnHeader) {
    if (columnHeader == null || columnHeader.trim().isEmpty()) {
      throw new IllegalArgumentException("El encabezado de columna no puede ser nulo o vacío");
    }

    // Manejar el caso donde el encabezado no está mapeado
    return columnFieldMap.getOrDefault(columnHeader, null);
  }

  /**
   * Agrega un nuevo mapeo de columna a campo, con validación para evitar conflictos.
   *
   * @param columnHeader el nombre del encabezado de columna en el Excel
   * @param fieldName el nombre del campo de la entidad
   */
  public void addMapping(String columnHeader, String fieldName) {
    if (columnHeader == null || columnHeader.trim().isEmpty() || fieldName == null || fieldName.trim().isEmpty()) {
      throw new IllegalArgumentException("Los encabezados de columna y nombres de campo no pueden ser nulos o vacíos");
    }

    columnFieldMap.putIfAbsent(columnHeader, fieldName);
  }

  /**
   * Devuelve un mapa inmutable de las configuraciones de mapeo actuales.
   *
   * @return un mapa inmutable de mapeos de columna a campo
   */
  public Map<String, String> getMappings() {
    return Map.copyOf(columnFieldMap);
  }

}