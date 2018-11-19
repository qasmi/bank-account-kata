package com.arolla.banck.acount.service;

import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class AccountServiceImplTest {

        private static final Instant NOW = Instant.parse("2018-02-05T15:00:00Z");
        public static final UUID ACCOUNT_ID = UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56459");
        public static final UUID INVALID_ACCOUNT_ID = UUID.fromString("9ded1d68-3dd2-451b-800e-7276d8c56440");
        public static final String CLIENT_ID = "93405676";
        private AccountService accountService;
        private AccountRepository accoutRepository;
        private Clock clock;
        @Before
        public void init(){

                clock = Clock.fixed(NOW, ZoneOffset.UTC);
                accoutRepository = new AccountRepository(clock);
                accountService = new AccountServiceImpl(accoutRepository, clock);
        }

        @Test
        public void deposit_should_save_2300_EUR(){

                Account actual = accountService.deposit(CLIENT_ID,ACCOUNT_ID, new BigDecimal(2300));

                assertThat(actual.getId()).isEqualTo(ACCOUNT_ID);
                assertThat(actual.getSolde()).isEqualTo(Solde.builder().amount(new BigDecimal(2600)).build());
                assertThat(actual.getCreationDate()).isEqualTo(NOW);
                assertThat(actual.getOwner()).isEqualTo(new Client("93405676", "arolla"));
                assertThat(actual.getHistory().size()).isEqualTo(1);
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
                assertThat(actual.getSolde()).isEqualTo(Solde.builder().amount(new BigDecimal(0)).build());
                assertThat(actual.getCreationDate()).isEqualTo(NOW);
                assertThat(actual.getOwner()).isEqualTo(new Client("93405676", "arolla"));
                assertThat(actual.getHistory().size()).isEqualTo(1);
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
}
