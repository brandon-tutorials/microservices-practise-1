package com.formacionbdi.springboot.app.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@EnableCircuitBreaker
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class SpringbootServicioItem1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioItem1Application.class, args);
	}

}
