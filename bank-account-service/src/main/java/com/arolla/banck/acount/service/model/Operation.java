package com.arolla.banck.acount.service.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Operation {
        @NonNull
        private final UUID id;
        @NonNull
        private final OperationType type ;
        @NonNull
        private final Instant creationDate;
        @NonNull
        private final BigDecimal amount;
}
