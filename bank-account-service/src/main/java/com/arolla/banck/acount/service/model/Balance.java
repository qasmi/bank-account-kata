package com.arolla.banck.acount.service.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@Builder
public class Balance {
        @NonNull
        private final BigDecimal amount;
        @Builder.Default
        private final Currency currency = Currency.EUR;
}
