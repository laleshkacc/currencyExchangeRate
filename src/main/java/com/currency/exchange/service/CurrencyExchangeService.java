package com.currency.exchange.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import com.currency.exchange.model.CurrencyExchange;
import com.currency.exchange.model.ExchangeRate;
import com.currency.exchange.repository.CurrencyExchangeRepository;
import com.currency.exchange.repository.ExchangeRateRepository;
import com.currency.exchange.utils.CurrencyExchangeRateLogConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyExchangeService {

  @Autowired
  private CurrencyExchangeRepository currencyExchangeRepository;

  @Autowired
  private ExchangeRateRepository exchangeRateRepository;

  public CurrencyExchange convertAmount(double amount, String sourceCurrency, String targetCurrency) throws IOException {
    validateInput(amount, sourceCurrency, targetCurrency);

    Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency);

    return exchangeRate.map(rate -> {
      double convertedAmount = amount * rate.getRate();
      CurrencyExchange currencyExchangeData =
          buildCurrencyExchange(sourceCurrency, targetCurrency, rate.getRate(), amount, convertedAmount);
      return currencyExchangeRepository.save(currencyExchangeData);
    }).orElseThrow(() -> new IllegalArgumentException("No se encontró una tasa de cambio de " + sourceCurrency + " a " + targetCurrency));
  }

  private void validateInput(double amount, String sourceCurrency, String targetCurrency) throws IOException {
    if (amount <= 0) {
      throw new IOException(CurrencyExchangeRateLogConstants.INVALID_AMOUNT);
    }
    if (sourceCurrency == null || sourceCurrency.isEmpty() || targetCurrency == null || targetCurrency.isEmpty()) {
      throw new IOException(CurrencyExchangeRateLogConstants.INVALID_CURRENCY);
    }
  }

  private CurrencyExchange buildCurrencyExchange(String sourceCurrency, String targetCurrency, double rate, double originalAmount,
      double convertedAmount) {
    return CurrencyExchange.builder().sourceCurrency(sourceCurrency).targetCurrency(targetCurrency).rate(rate)
        .originalAmount(originalAmount).convertedAmount(convertedAmount).conversionDate(LocalDateTime.now()).build();
  }
}
