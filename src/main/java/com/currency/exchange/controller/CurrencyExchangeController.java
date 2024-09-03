package com.currency.exchange.controller;

import java.io.IOException;

import com.currency.exchange.model.CurrencyExchange;
import com.currency.exchange.service.CurrencyExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
  public CurrencyExchange convert(@RequestParam double amount, @RequestParam String sourceCurrency, @RequestParam String targetCurrency)
      throws IOException {
    CurrencyExchange currencyExchange = currencyExchangeService.convertAmount(amount, sourceCurrency, targetCurrency);
    return ResponseEntity.ok(currencyExchange).getBody();
  }
}
