package com.awslambdaconvert.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping
    public HealthCheckMessage checkStatus() {
        return new HealthCheckMessage(true);
    }
}
