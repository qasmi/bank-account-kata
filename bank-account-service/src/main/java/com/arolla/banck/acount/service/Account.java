package com.arolla.banck.acount.service;

import lombok.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        private Solde solde;
        @Builder.Default
        private Set<Operation> history =  new HashSet<>();

        public Account deposit(BigDecimal amount, Clock clock){
                updateSolde(amount);
                updateHistory(amount, clock);
                return this;
        }

        public Account withdraw(BigDecimal amount, Clock clock){
                if(solde.getAmount().compareTo(amount) == -1){
                        throw new OperationNotPermittedException("Insufficient balance");
                }
                updateSolde(amount.multiply(new BigDecimal(-1)));
                updateHistory(amount, clock);
                return this;
        }

        private void updateHistory(BigDecimal amount, Clock clock) {
                Operation operation = Operation.builder()
                        .id(UUID.randomUUID())
                        .creationDate(clock.instant())
                        .amount(amount)
                        .status(OperationStatus.valide)
                        .type(OperationType.DEPOSIT)
                        .build();
                history.add(operation);
        }

        private void updateSolde(BigDecimal amount) {
                solde = Solde.builder()
                        .amount(solde.getAmount().add(amount))
                        .currency(solde.getCurrency())
                        .build();
        }
}
