package com.arolla.banck.acount.service;

import com.arolla.banck.acount.service.exception.AccountNotFoundException;
import com.arolla.banck.acount.service.exception.OperationNotPermittedException;
import com.arolla.banck.acount.service.model.*;
import com.sun.media.sound.SF2Modulator;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class AccountServiceImplTest {

        private static final Instant NOW = Instant.parse("2018-02-05T15:00:00Z");
        public static final UUID ACCOUNT_ID = UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56459");
        public static final UUID INVALID_ACCOUNT_ID = UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56440");
        public static final String CLIENT_ID = "93405676";
        public static final Client CLIENT1 = Client.builder().id(CLIENT_ID).name("arolla").build();
        private AccountService accountService;
        private AccountRepository accountRepository;
        private Clock clock;
        private List<Account> accounts = new ArrayList<>();
        @Before
        public void init(){
                Account account = Account.builder()
                        .id(ACCOUNT_ID)
                        .owner(CLIENT1)
                        .creationDate(NOW)
                        .balance(Balance.builder().amount(new BigDecimal(300))
                                .build())
                        .build();
                clock = Clock.fixed(NOW, ZoneOffset.UTC);
                accounts.add(account);
                accountRepository = new AccountRepository(accounts);
                accountService = new AccountServiceImpl(accountRepository, clock);
        }

        @Test
        public void deposit_should_save_2300_EUR(){

                Account actual = accountService.deposit(CLIENT_ID,ACCOUNT_ID, new BigDecimal(2300));

                assertThat(actual.getId()).isEqualTo(ACCOUNT_ID);
                assertThat(actual.getBalance()).isEqualTo(Balance.builder().amount(new BigDecimal(2600)).build());
                assertThat(actual.getCreationDate()).isEqualTo(NOW);
                assertThat(actual.getOwner()).isEqualTo(CLIENT1);
                assertThat(actual.getHistory().size()).isEqualTo(1);
                Operation operation = actual.getHistory().get(0);
                assertThat(operation.getAmount()).isEqualTo(new BigDecimal(2300));
                assertThat(operation.getType()).isEqualTo(OperationType.DEPOSIT);
                assertThat(operation.getCreationDate()).isEqualTo(NOW);
        }

        @Test
        public void deposit_should_throw_account_not_found_exception(){

                assertThatThrownBy(() -> accountService.deposit(CLIENT_ID, INVALID_ACCOUNT_ID, new BigDecimal(2300)))
                        .isInstanceOf(AccountNotFoundException.class)
                        .hasMessageStartingWith("Account {} not found " + INVALID_ACCOUNT_ID);
        }

        @Test
        public void withdraw_should_permit_retrieving_300_EUR(){

                Account actual = accountService.withdraw(CLIENT_ID,ACCOUNT_ID, new BigDecimal(300));

                assertThat(actual.getId()).isEqualTo(ACCOUNT_ID);
                assertThat(actual.getBalance()).isEqualTo(Balance.builder().amount(new BigDecimal(0)).build());
                assertThat(actual.getCreationDate()).isEqualTo(NOW);
                assertThat(actual.getOwner()).isEqualTo(CLIENT1);
                assertThat(actual.getHistory().size()).isEqualTo(1);
                Operation operation = actual.getHistory().get(0);
                assertThat(operation.getAmount()).isEqualTo(new BigDecimal(300));
                assertThat(operation.getType()).isEqualTo(OperationType.WITHDRAW);
                assertThat(operation.getCreationDate()).isEqualTo(NOW);
        }

        @Test
        public void withdraw_should_throw_account_not_found_exception(){

                assertThatThrownBy(() -> accountService.withdraw(CLIENT_ID, INVALID_ACCOUNT_ID, new BigDecimal(2300)))
                        .isInstanceOf(AccountNotFoundException.class)
                        .hasMessageStartingWith("Account {} not found " + INVALID_ACCOUNT_ID);
        }

        @Test
        public void withdraw_should_throw_operation_not_permitted_exception_when_retrieving_amount_is_more_than_balance() {
                assertThatThrownBy(() -> accountService.withdraw(CLIENT_ID, ACCOUNT_ID, new BigDecimal(2300)))
                        .isInstanceOf(OperationNotPermittedException.class)
                        .hasMessageStartingWith("Insufficient balance");

        }

        @Test
        public void showHistory_should_return_all_operation_history_of_my_account() {
                Operation operation1 = buildOperation(UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56470"),
                        NOW.minusSeconds(3), new BigDecimal(50), OperationType.DEPOSIT);
                Operation operation2 = buildOperation(UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56471"),
                        NOW.minusSeconds(6), new BigDecimal(80), OperationType.WITHDRAW);
                Operation operation3 = buildOperation(UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56472"),
                        NOW.minusSeconds(12), new BigDecimal(40), OperationType.DEPOSIT);
                List<Operation> operations = Lists.newArrayList(operation3, operation2, operation1);
                Account account = Account.builder()
                        .id(UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56444"))
                        .owner(CLIENT1)
                        .creationDate(NOW)
                        .balance(Balance.builder().amount(new BigDecimal(300))
                                .build())
                        .history(operations)
                        .build();
                accounts.add(account);
                accountRepository = new AccountRepository(accounts);
                accountService = new AccountServiceImpl(accountRepository, clock);

                AccountHistoryResponse accountHistoryResponse = accountService.getHistory(CLIENT_ID,UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56444"));
                assertThat(accountHistoryResponse.getBalance()).isEqualTo(Balance.builder().amount(new BigDecimal(300))
                        .build());
                List<Operation> history = accountHistoryResponse.getHistory();
                assertThat(history).hasSize(3);
                assertThat(history.get(0)).isEqualTo(operation1);
                assertThat(history.get(1)).isEqualTo(operation2);
                assertThat(history.get(2)).isEqualTo(operation3);
        }

        private Operation buildOperation(UUID id, Instant creationDate, BigDecimal amount, OperationType type) {
                return Operation.builder()
                        .id(id)
                        .creationDate(creationDate)
                        .amount(amount)
                        .type(type)
                        .build();
        }

        @Test
        public void showHistory_should_throw_account_not_found_exception(){
                assertThatThrownBy(() -> accountService.getHistory(CLIENT_ID, INVALID_ACCOUNT_ID))
                        .isInstanceOf(AccountNotFoundException.class)
                        .hasMessageStartingWith("Account {} not found " + INVALID_ACCOUNT_ID);
        }


}
