package com.formacionbdi.springboot.app.gateway.filters;
 

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class EjemploGlobalFilter implements GlobalFilter,Ordered{

	private final Logger log = LoggerFactory.getLogger(EjemploGlobalFilter.class);
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("Ejecutando filtro pre");
		exchange.getRequest().mutate().headers( h -> h.add("token", "123456"));
		return chain.filter(exchange).then(Mono.fromRunnable(()->{
			log.info("Ejecutando filtro post");
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(valor ->{
			exchange.getResponse().getHeaders().add("token", valor);
			});
			
			exchange.getResponse().getCookies().add("color",ResponseCookie.from("color", "azul").build());
			//exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
		}));
	}

	@Override
	public int getOrder() {
		return 1;
	}
	
}
















/*Nos permite crear un elemento reactivo 
el metodo filter devuelve un Mono<Void>

Dentro del fromRunnable implementamos la tarea que viene despues del PRE
Es decir el POS.

Solo lo utilizamos para crear el elemento MONO
Se crea mediante una funcion lamda

TODO LO QUE ESTA ARRIBA DEL RETURN ES EL PRE
TODO LO QUE ESTA EN LA FUNCION LAMBDA ES EL POS
*/