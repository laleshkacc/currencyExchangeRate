package com.currency.exchange.utils;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CurrencyExchangeRateConstants {

  public static final int BATCH_SIZE = 1000;

  public static final String ID = "ID";

  public static final String SOURCE_CURRENCY = "MonedaOrigen";

  public static final String TARGET_CURRENCY = "MonedaDestino";

  public static final String RATE = "Tasa";

  public static final String ID_FIELD = "idExchangeRate";

  public static final String SOURCE_CURRENCY_FIELD = "sourceCurrency";

  public static final String TARGET_CURRENCY_FIELD = "targetCurrency";

  public static final String RATE_FIELD = "rate";

}
