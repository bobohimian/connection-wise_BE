package com.conwise.model;

import lombok.Data;
import reactor.core.publisher.Flux;

@Data
public class FluxWrapper {
    private String prompt;
    private String format;
    public Flux<String> call;
}
