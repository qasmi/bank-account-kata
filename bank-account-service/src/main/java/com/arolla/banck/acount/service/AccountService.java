package com.arolla.banck.acount.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountService {

        Account deposit(String clientId, UUID accountId, BigDecimal amount);

        Account withdraw(String clientId, UUID accountId, BigDecimal bigDecimal);
}
