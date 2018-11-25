package com.arolla.banck.acount.service;

import com.arolla.banck.acount.service.exception.AccountNotFoundException;
import com.arolla.banck.acount.service.exception.OperationNotPermittedException;
import com.arolla.banck.acount.service.model.*;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class AccountManagerTest {

        private static final Instant NOW = Instant.parse("2018-11-25T15:00:00Z");
        public static final UUID ACCOUNT_ID = UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56459");
        public static final UUID INVALID_ACCOUNT_ID = UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56440");
        public static final Client CLIENT1 = Client.builder().id("93405676").name("arolla").build();
        private AccountApi accountService;
        private InMemoryAccountRepository accountRepository;
        private Clock clock;
        private Map<UUID, Account> accounts;
        @Before
        public void init(){
                Balance balance = buildBalance(bigDecimal(300));
                Account account = Account.builder()
                        .id(ACCOUNT_ID)
                        .owner(CLIENT1)
                        .creationDate(NOW)
                        .balance(balance)
                        .build();
                clock = Clock.fixed(NOW, ZoneOffset.UTC);
                accounts = new HashMap<>();
                accounts.put(ACCOUNT_ID, account);
                accountRepository = new InMemoryAccountRepository(accounts);
                accountService = new AccountManager(accountRepository, clock);
        }

        @Test
        public void deposit_allow_saving_money_in_known_account(){
                Balance expectedBalance = buildBalance(bigDecimal(2600));
                Account actual = accountService.deposit(ACCOUNT_ID, bigDecimal(2300));

                assertThat(actual.getId()).isEqualTo(ACCOUNT_ID);
                assertThat(actual.getBalance()).isEqualTo(expectedBalance);
                assertThat(actual.getCreationDate()).isEqualTo(NOW);
                assertThat(actual.getOwner()).isEqualTo(CLIENT1);
        }

        @Test
        public void deposit_store_one_deposit_operation(){
                Account actual = accountService.deposit(ACCOUNT_ID, bigDecimal(2300));

                assertThat(actual.getHistory().size()).isEqualTo(1);
                Operation operation = actual.getHistory().get(0);
                assertThat(operation.getAmount()).isEqualTo(bigDecimal(2300));
                assertThat(operation.getType()).isEqualTo(OperationType.DEPOSIT);
                assertThat(operation.getCreationDate()).isEqualTo(NOW);
        }

        @Test
        public void deposit_not_allow_deposing_money_into_an_unknown_account(){
                assertThatThrownBy(() -> accountService.deposit(INVALID_ACCOUNT_ID, bigDecimal(2300)))
                        .isInstanceOf(AccountNotFoundException.class)
                        .hasMessage(String.format("Account %s not found ", INVALID_ACCOUNT_ID));
        }

        @Test
        public void withdraw_allow_retrieving_amount_from_known_account(){
                Balance expectedBalance = buildBalance(bigDecimal(0));
                Account actual = accountService.withdraw(ACCOUNT_ID, bigDecimal(300));

                assertThat(actual.getId()).isEqualTo(ACCOUNT_ID);
                assertThat(actual.getBalance()).isEqualTo(expectedBalance);
                assertThat(actual.getCreationDate()).isEqualTo(NOW);
                assertThat(actual.getOwner()).isEqualTo(CLIENT1);
        }

        @Test
        public void withdraw_store_one_withdraw_operation(){
                Account actual = accountService.withdraw(ACCOUNT_ID, bigDecimal(300));

                assertThat(actual.getHistory().size()).isEqualTo(1);
                Operation operation = actual.getHistory().get(0);
                assertThat(operation.getAmount()).isEqualTo(bigDecimal(300));
                assertThat(operation.getType()).isEqualTo(OperationType.WITHDRAW);
                assertThat(operation.getCreationDate()).isEqualTo(NOW);
        }

        @Test
        public void withdraw_not_allow_withdrawing_money_from_an_unknown_account(){
                assertThatThrownBy(() -> accountService.withdraw( INVALID_ACCOUNT_ID, bigDecimal(2300)))
                        .isInstanceOf(AccountNotFoundException.class)
                        .hasMessage(String.format("Account %s not found ", INVALID_ACCOUNT_ID));
        }

        @Test
        public void withdraw_not_allow_withdrawing_money_when_balance_is_less_than_amount_to_withdraw() {
                assertThatThrownBy(() -> accountService.withdraw(ACCOUNT_ID, bigDecimal(2300)))
                        .isInstanceOf(OperationNotPermittedException.class)
                        .hasMessage("Insufficient balance");
        }

        @Test
        public void getHistory_should_return_history_in_order() {
                Balance balance = buildBalance(bigDecimal(300));
                Operation operation1 = buildOperation(UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56470"),
                        NOW.minusSeconds(3), bigDecimal(50), OperationType.DEPOSIT);
                Operation operation2 = buildOperation(UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56471"),
                        NOW.minusSeconds(6), bigDecimal(80), OperationType.WITHDRAW);
                Operation operation3 = buildOperation(UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56472"),
                        NOW.minusSeconds(12), bigDecimal(40), OperationType.DEPOSIT);
                List<Operation> operations = Lists.newArrayList(operation3, operation2, operation1);
                UUID accountUuid = UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56444");
                Account account = Account.builder()
                        .id(accountUuid)
                        .owner(CLIENT1)
                        .creationDate(NOW)
                        .balance(balance)
                        .history(operations)
                        .build();
                accounts.put(accountUuid, account);
                AccountHistoryResponse expectedAccountHistoryResponse = AccountHistoryResponse.builder()
                        .balance(balance)
                        .history( Lists.newArrayList(operation1, operation2, operation3))
                        .build();
                accountRepository = new InMemoryAccountRepository(accounts);
                accountService = new AccountManager(accountRepository, clock);

                AccountHistoryResponse actualAccountHistoryResponse = accountService.getHistory(accountUuid);
                assertThat(actualAccountHistoryResponse).isEqualTo(expectedAccountHistoryResponse);
        }

        @Test
        public void getHistory_can_not_get_operation_history_of_an_unknown_account(){
                assertThatThrownBy(() -> accountService.getHistory(INVALID_ACCOUNT_ID))
                        .isInstanceOf(AccountNotFoundException.class)
                        .hasMessage(String.format("Account %s not found ", INVALID_ACCOUNT_ID));
        }

        private Operation buildOperation(UUID id, Instant creationDate, BigDecimal amount, OperationType type) {
                return Operation.builder()
                        .id(id)
                        .creationDate(creationDate)
                        .amount(amount)
                        .type(type)
                        .build();
        }

        private BigDecimal bigDecimal(int i) {
                return BigDecimal.valueOf(i).setScale(2, RoundingMode.HALF_UP);
        }

        private Balance buildBalance(BigDecimal amount){
                return Balance.builder().amount(amount).build();
        }
}
