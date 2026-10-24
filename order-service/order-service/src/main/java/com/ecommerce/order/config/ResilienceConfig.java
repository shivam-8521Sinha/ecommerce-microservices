package com.ecommerce.order.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.cloud.client.circuitbreaker.observation.CircuitBreakerObservationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ResilienceConfig {
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {

        return CircuitBreakerConfig.ofDefaults();
    }
}
