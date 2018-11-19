package com.arolla.banck.acount.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Client {

        private final String id;
        private final String name;
}
