package com.currency.exchange.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class CurrencyExchange {

  @Id
  private Integer idCurrency;

  private String sourceCurrency;

  private String targetCurrency;

  private double rate;

  private double originalAmount;

  private double convertedAmount;

  private LocalDateTime conversionDate;

}
