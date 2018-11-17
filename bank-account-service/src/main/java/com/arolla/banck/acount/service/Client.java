package com.arolla.banck.acount.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Client {

        private final String id;
        private final String name;
}
