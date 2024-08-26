package com.currency.exchange.controller;

import java.io.IOException;
import java.util.List;

import com.currency.exchange.model.ExchangeRate;
import com.currency.exchange.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ExchangeRateController {

  @Autowired
  private ExchangeRateService exchangeRateService;

  @PostMapping("/rate")
  public List<ExchangeRate> createExchangeRate(@RequestParam("file") MultipartFile file) throws IOException {
    return exchangeRateService.saveExchangeRate(file);
  }

  @GetMapping("/rate")
  public List<ExchangeRate> getExchangeRate() {
    return exchangeRateService.getExchangeRate();
  }

  @PutMapping("/rate/{id}")
  public ExchangeRate updateExchangeRate(@PathVariable Integer id, @RequestBody ExchangeRate rate) {
    return exchangeRateService.updateExchangeRate(id, rate);
  }
}
