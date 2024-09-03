package com.currency.exchange.utils;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CurrencyExchangeRateLogConstants {

  public static final String ERROR_INVALID_FILE_FORMAT = "Formato de archivo no válido o datos faltantes.";

  public static final String ERROR_INVALID_PROCESS = "Error al procesar el archivo: ";

  public static final String ERROR_INVALID_MAPP_EXCHANGERATE = "Error al mapear la fila a ExchangeRate: ";

  public static final String ERROR_CONVERT_DOUBLE = "Error al convertir a Double: ";

  public static final String ERROR_SAVE_TYPE_CHANGE = "Error al guardar los tipos de cambio desde el archivo Excel: ";

  public static final String INVALID_AMOUNT = "La moneda especificada no es válida.";

  public static final String INVALID_CURRENCY = "El monto especificado no es válido.";

  public static final String ERROR_EXCHANGE_RATE_NOT_FOUND = "Tipo de cambio no encontrado";

  public static final String ERROR_SAVE_FILE_STORAGE = "No se pudo crear el directorio de almacenamiento de archivos";

  public static final String ERROR_NOT_FOUND_FILE = "El archivo no se encuentra ";

  public static final String ERROR_NOT_FOUND_FILE_ROUTE = "El archivo no se encuentra en la ruta especificada";

  public static final String ERROR_INVALID_NAME = "Nombre de archivo inválido";

  public static final String ERROR_INVALID_FILE = "Archivo no proporcionado";

}
