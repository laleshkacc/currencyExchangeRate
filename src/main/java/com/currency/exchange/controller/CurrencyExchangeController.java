package com.currency.exchange.controller;

import com.currency.exchange.model.CurrencyExchange;
import com.currency.exchange.service.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CurrencyExchangeController {

  @Autowired
  private CurrencyExchangeService currencyExchangeService;

  @GetMapping("/convert")
  public CurrencyExchange convert(@RequestParam double amount, @RequestParam String sourceCurrency, @RequestParam String targetCurrency) {
    return currencyExchangeService.convertAmount(amount, sourceCurrency, targetCurrency);
  }
}
