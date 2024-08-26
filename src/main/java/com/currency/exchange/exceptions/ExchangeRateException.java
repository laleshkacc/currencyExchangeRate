package com.currency.exchange.exceptions;

public class ExchangeRateException extends RuntimeException {

  public ExchangeRateException(String monedaOriginal, String monedaDestino) {
    super(String.format("Tipo de cambio no encontrado para %s a %s", monedaOriginal, monedaDestino));
  }

}
