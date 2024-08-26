package com.currency.exchange.repository;

import java.util.Optional;

import com.currency.exchange.model.CurrencyExchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long> {

}
