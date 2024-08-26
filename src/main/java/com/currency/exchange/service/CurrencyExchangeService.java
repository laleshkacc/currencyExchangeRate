package com.currency.exchange.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.currency.exchange.exceptions.ExchangeRateException;
import com.currency.exchange.model.CurrencyExchange;
import com.currency.exchange.model.ExchangeRate;
import com.currency.exchange.repository.CurrencyExchangeRepository;
import com.currency.exchange.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyExchangeService {

  @Autowired
  private CurrencyExchangeRepository currencyExchangeRepository;

  @Autowired
  private ExchangeRateRepository exchangeRateRepository;

  public CurrencyExchange convertAmount(double amount, String sourceCurrency, String targetCurrency) {
    Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency);

    if (exchangeRate.isPresent()) {
      ExchangeRate currencyExchange = exchangeRate.get();
      double rate = currencyExchange.getRate();
      double convertedAmount = amount * rate;

      CurrencyExchange currencyExchangeData = CurrencyExchange.builder()
          .sourceCurrency(sourceCurrency)
          .targetCurrency(targetCurrency)
          .rate(rate)
          .originalAmount(amount)
          .convertedAmount(convertedAmount)
          .conversionDate(LocalDateTime.now())
          .build();
      return currencyExchangeRepository.save(currencyExchangeData);
    } else {
      throw new ExchangeRateException(sourceCurrency, targetCurrency);
    }
  }

  public CurrencyExchange updateExchangeRate(Long id, CurrencyExchange newRate) {
    return currencyExchangeRepository.findById(id).map(rate -> {
      rate.setSourceCurrency(newRate.getSourceCurrency());
      rate.setTargetCurrency(newRate.getTargetCurrency());
      rate.setRate(newRate.getRate());
      rate.setOriginalAmount(newRate.getOriginalAmount());
      rate.setConvertedAmount(newRate.getConvertedAmount());
      return currencyExchangeRepository.save(rate);
    }).orElseThrow(() -> new IllegalArgumentException("Tipo de cambio no encontrado"));
  }
}
