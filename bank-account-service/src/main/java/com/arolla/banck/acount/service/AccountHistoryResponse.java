package com.arolla.banck.acount.service;

import com.arolla.banck.acount.service.model.Balance;
import com.arolla.banck.acount.service.model.Operation;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountHistoryResponse {
    private final Balance balance;
    private final List<Operation> history;
}
