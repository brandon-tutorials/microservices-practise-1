package com.formacionbdi.springboot.app.item.controllers;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.springboot.app.item.models.Item;
import com.formacionbdi.springboot.app.item.models.Producto;
import com.formacionbdi.springboot.app.item.service.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/*PERMITE REFRESCAR LOS BEANS DEL CONTEXTO QUE ESTAMOS INYECTANDO*/
@RefreshScope 
/*PERMITE FRESCAR*/
@RestController
public class ItemController {
	
	private static Logger log = LoggerFactory.getLogger(ItemController.class);
	
	@Autowired
	private Environment env;
	
	
	@Autowired
	/*PARA EL CIRCUIT BREAKER FACTORY SOLO FUNCIONA LA CONFIGURACION EN EL ARCHIV CONFIG*/
	private CircuitBreakerFactory cbFactory;
	
	@Autowired
	@Qualifier("itemServiceFeign")
	private ItemService itemService;
	
	@Value("${configuracion.texto}")
	private String texto;
	 
	@GetMapping("/listar")
	public List<Item> listar(@RequestParam(name="nombre",required=false)String nombre,@RequestHeader(name="token-request",required=false)String token){
		System.out.println(nombre);
		System.out.println(token);
		return itemService.findAll();
	}

	//@HystrixCommand(fallbackMethod="metodoAlternativo")
	@GetMapping("/ver/{id}/cantidad/{cantidad}")
	public Item detalle(@PathVariable Long id, @PathVariable Integer cantidad) {
		return cbFactory.create("items")
				.run(()-> itemService.findById(id, cantidad),/*ES UN METODO ALTERNATIVO*/ e -> metodoAlternativo(id,cantidad,e));
		//return itemService.findById(id, cantidad);
	}
	/*PARA LA ANOTACION CIRCUIT BREAKER SOLO FUNCIONA LA CONFIGURACION DE LOS ARCHIVOS YAML O PROPERTIES */
	@CircuitBreaker(name="items",fallbackMethod = "metodoAlternativo")
	@GetMapping("/ver2/{id}/cantidad/{cantidad}")
	public Item detalle2(@PathVariable Long id, @PathVariable Integer cantidad) {
		return itemService.findById(id, cantidad);
	}
	
	@TimeLimiter(name="items",fallbackMethod = "metodoAlternativo2")/*AQUI SOLO SE APLICA EL TIME OUT, NO EL CORTO CIRCUITO*/
	@GetMapping("/ver3/{id}/cantidad/{cantidad}")
	public CompletableFuture<Item> detalle3(@PathVariable Long id, @PathVariable Integer cantidad) {
		return CompletableFuture.supplyAsync(() -> itemService.findById(id, cantidad)
				) ; /*
				ESTAMOS ENVOLVIENDO EL METODO EN UNA REPRESENTACION FUTURA,
				EN UNA LLAMADA FUTURA, ASINCRONA PARA CALCULAR EL TIEMPO DE ESPERA
			*/
	}
	
	/*CUANDO TENEMOS LAS 2 ANOTACIONES JUNTAS, SOLO DEBEMOS ESPECIFICAR UN METODO ALTERNATIVO
	 * EL METODO DEBE ESTAR EN CIRCUITBREAKER
	 * DE ESTA MANERA GARANTIZAMOS EL CIRCUITBREAKER  POR EXCEPCION Y POR TIMEOUT
	 * 
	 * CUANDO ESTABAN LOS 2 METODOS ALTERNATIVOS NO GARANTIZAMOS EL CIRCUITBREAKER POR EXCEPCION 
	 * 
	 * */
	@CircuitBreaker(name="items",fallbackMethod = "metodoAlternativo2")
	@TimeLimiter(name="items")/*AQUI SOLO SE APLICA EL TIME OUT, NO EL CORTO CIRCUITO*/
	@GetMapping("/ver4/{id}/cantidad/{cantidad}")
	public CompletableFuture<Item> detalle4(@PathVariable Long id, @PathVariable Integer cantidad) {
		return CompletableFuture.supplyAsync(() -> itemService.findById(id, cantidad)
				) ; /*
				ESTAMOS ENVOLVIENDO EL METODO EN UNA REPRESENTACION FUTURA,
				EN UNA LLAMADA FUTURA, ASINCRONA PARA CALCULAR EL TIEMPO DE ESPERA
			*/
	}
	
	public Item metodoAlternativo(Long id,Integer cantidad, Throwable e) {
		log.info(e.getMessage());
		Item item = new Item();
		Producto producto = new Producto();
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Camara Sony");
		producto.setPrecio(500.0);
		item.setProducto(producto);
		return item;
	}
	
	public CompletableFuture<Item> metodoAlternativo2(Long id,Integer cantidad, Throwable e) {
		log.info(e.getMessage());
		Item item = new Item();
		Producto producto = new Producto();
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Camara Sony");
		producto.setPrecio(500.0);
		item.setProducto(producto);
		return CompletableFuture.supplyAsync(()->item);
	}
	
	@GetMapping("/obtener-config")
	public ResponseEntity<?> obtenerConfig(@Value("${server.port}")String puerto){
		log.info(texto);
		Map<String,String> json = new HashMap<String,String>();
		json.put("texto", texto);
		json.put("puerto",puerto);
		
		/*CON ESTO YA SABEMOS QUE TENEMOS CONFIGURADO UN PROFILE*/
		if(env.getActiveProfiles().length>0 && env.getActiveProfiles()[0].equals("dev")){
			json.put("autor.nombre", env.getProperty("configuracion.autor.nombre"));
			json.put("autor.email", env.getProperty("configuracion.autor.email"));
		} 
		
		return new ResponseEntity<Map<String,String>>(json,HttpStatus.OK);
	}
	
	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto crear(@RequestBody Producto producto) {
		return itemService.save(producto);
	}
	
	@PutMapping("/editar/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto editar(@RequestBody Producto producto, @PathVariable Long id) {
		return itemService.update(producto, id);
	}
	
	@DeleteMapping("/eliminar/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		itemService.delete(id);
	}
	
	
}
