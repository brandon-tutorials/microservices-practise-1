package com.formacionbdi.springboot.app.gateway.filters.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

 
import reactor.core.publisher.Mono;

@Component
public class EjemploGatewayFilterFactory extends AbstractGatewayFilterFactory<EjemploGatewayFilterFactory.Configuracion>{

	private final Logger log = LoggerFactory.getLogger(EjemploGatewayFilterFactory.class);


	public EjemploGatewayFilterFactory() {
		super(Configuracion.class);
	}

	@Override
	public GatewayFilter apply(Configuracion config) {
		
		/*LA CLASE QUE ORDENA NUESTRO FILTRO
		 * COMO SEGUNDO ARGUMENTO ES EL NUMERO DE ORDEN QUE QUEREMOS DAR
		 * 
		 *  */
		return new OrderedGatewayFilter((exchange,chain)->{
			log.info("Ejecutando pre gateway filter factory"+ config.mensaje);
			return chain.filter(exchange).then(
					Mono.fromRunnable(()->{
						
						Optional.ofNullable(config.cookieValor).ifPresent(Cookie -> {
							exchange.getResponse().addCookie(ResponseCookie.from(config.cookieNombre, config.cookieValor).build());
						});
						
						log.info("Ejecutando post gateway filter factory"+config.mensaje);
					})
					);
		},2);
	}
	
	
	
	@Override
	public String name() {
		return "EjemploCookie";
	}

	/*EL ORDEN  QUE DEBE SEGUIR EN NUESTRO ARCHIVO YAML*/
	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("mensaje","cookieNombre","cookieValor");
	}

	public static class Configuracion {
		
		private String mensaje;
		private String cookieValor;
		private String cookieNombre;
		
		public String getMensaje() {
			return mensaje;
		}
		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}
		public String getCookieValor() {
			return cookieValor;
		}
		public void setCookieValor(String cookieValor) {
			this.cookieValor = cookieValor;
		}
		public String getCookieNombre() {
			return cookieNombre;
		}
		public void setCookieNombre(String cookieNombre) {
			this.cookieNombre = cookieNombre;
		}

	}

}
