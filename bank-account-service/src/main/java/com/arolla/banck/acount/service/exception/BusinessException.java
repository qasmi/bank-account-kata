package com.arolla.banck.acount.service.exception;

public abstract class BusinessException extends RuntimeException {
        private static final long serialVersionUID = 523600001337000032L;

        public BusinessException(String message, Throwable cause) {
                super(message, cause);
        }

        public BusinessException(String message) {
                super(message);
        }
}