package com.formacionbdi.springboot.app.kafka;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration 
public class AppConfig {

	@Bean("clienteRest") 
	@LoadBalanced
	public RestTemplate registrarRestTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer(){
		return factory -> factory.configureDefault( id ->{
			return new Resilience4JConfigBuilder(id)
					.circuitBreakerConfig(CircuitBreakerConfig.custom().slidingWindowSize(10) /* TOTAL DE PETICIONES TOMADAS EN CUENTA PARA HACER EL PROMEDIO QUE DETERMINARA EL CORTO CIRCUITO*/
							.failureRateThreshold(50) /*PORCENTAJE DE FALLAS PARA LAS PETICIONES, SI SUPERA EL PORCENTAJE ENTRA A CORTO CIRCUITO*/
							.waitDurationInOpenState(Duration.ofSeconds(10L)) /*CUANDO OCURRE EL CORTOCIRCUITO, CUANTO TIEMPO SE ESPERA PARA VOLVER A CONSIDERAR UN PROMEDIO*/
							.permittedNumberOfCallsInHalfOpenState(5) /* NUMERO DE LLAMADAS EN ESTADO SEMIABIERTO*/
							.slowCallRateThreshold(50) /*ES EL PORCENTAJE DE LLAMADA LENTA, SI SUPERA ESE PORCENTAJE SE ABRE EL CORTO CIRCUITO*/
							.slowCallDurationThreshold(Duration.ofSeconds(2L)) /*ES EL TIEMPO DE LLAMADA LENTA*/
							.build())
					.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(6L)).build()) /* ES EL TIEMPO DE TIMEOUT*/
					.build();
		}); 
		
	}
	
}
