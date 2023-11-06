package com.formacionbdi.springboot.app.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class SpringbootServicioItem2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioItem2Application.class, args);
	}

}
