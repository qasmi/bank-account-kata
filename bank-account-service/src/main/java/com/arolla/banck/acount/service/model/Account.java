package com.arolla.banck.acount.service.model;

import com.arolla.banck.acount.service.exception.OperationNotPermittedException;
import lombok.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.*;

@Data
@Builder
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
public class Account {

        @NonNull
        private UUID id;
        @NonNull
        private Instant creationDate;
        @NonNull
        private Client owner;
        @NonNull
        private Balance balance;
        @Builder.Default
        private List<Operation> history =  new ArrayList<>();

        public void deposit(BigDecimal amount, Clock clock){
                updateBalance(amount);
                updateHistory(OperationType.DEPOSIT, amount, clock);
        }

        public void withdraw(BigDecimal amount, Clock clock){
                if(balance.isLessThan(amount)){
                        throw new OperationNotPermittedException("Insufficient balance");
                }
                updateBalance(amount.multiply(BigDecimal.valueOf(-1)));
                updateHistory(OperationType.WITHDRAW, amount, clock);
        }

        private void updateHistory(OperationType operationType, BigDecimal amount, Clock clock) {
                Operation operation = Operation.builder()
                        .id(UUID.randomUUID())
                        .creationDate(clock.instant())
                        .amount(amount)
                        .type(operationType)
                        .build();
                history.add(operation);
        }

        private void updateBalance(BigDecimal amount) {
                balance = Balance.builder()
                        .amount(balance.getAmount().add(amount))
                        .currency(balance.getCurrency())
                        .build();
        }
}
