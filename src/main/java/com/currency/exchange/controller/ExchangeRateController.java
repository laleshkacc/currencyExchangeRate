package com.currency.exchange.controller;

import java.util.List;

import com.currency.exchange.model.ExchangeRate;
import com.currency.exchange.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/exchange-rates")
public class ExchangeRateController {

  private final ExchangeRateService exchangeRateService;

  @Autowired
  public ExchangeRateController(ExchangeRateService exchangeRateService) {
    this.exchangeRateService = exchangeRateService;
  }

  @PostMapping("/upload")
  public ResponseEntity<List<ExchangeRate>> uploadExchangeRates(MultipartFile file) {
    List<ExchangeRate> exchangeRates = exchangeRateService.saveExchangeRates(file);
    return ResponseEntity.ok(exchangeRates);
  }

  @GetMapping
  public ResponseEntity<List<ExchangeRate>> getAllExchangeRates() {
    return ResponseEntity.ok(exchangeRateService.getExchangeRates());
  }

  @PutMapping("/{id}")
  public ResponseEntity<ExchangeRate> updateExchangeRate(@PathVariable Integer id, @RequestBody ExchangeRate newRate) {
    ExchangeRate updatedRate = exchangeRateService.updateExchangeRate(id, newRate);
    return ResponseEntity.ok(updatedRate);
  }
}
