package com.arolla.banck.acount.service;

import com.arolla.banck.acount.service.model.Account;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountApi {

        Account deposit(UUID accountId, BigDecimal amount);

        Account withdraw(UUID accountId, BigDecimal bigDecimal);

        AccountHistoryResponse getHistory(UUID accountId);
}
