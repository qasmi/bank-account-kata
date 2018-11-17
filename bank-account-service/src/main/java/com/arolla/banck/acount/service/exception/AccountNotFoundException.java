package com.arolla.banck.acount.service.exception;


public class AccountNotFoundException extends BusinessException {
        private static final long serialVersionUID = 8165936063601631290L;

        public AccountNotFoundException(String message) {
                super(message);
        }

        public AccountNotFoundException(String message, Exception e) {
                super(message, e);
        }
}

