package com.arolla.banck.acount.service;


public class OperationNotPermittedException extends  BusinessException {
        private static final long serialVersionUID = 8165936063601631290L;

        public OperationNotPermittedException(String message) {
                super(message);
        }

        public OperationNotPermittedException(String message, Exception e) {
                super(message, e);
        }
}

