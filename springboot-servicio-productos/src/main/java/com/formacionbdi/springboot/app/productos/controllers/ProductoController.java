package com.formacionbdi.springboot.app.productos.controllers;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.springboot.app.commons.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.service.IProductoService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
@RestController
public class ProductoController {
	
	static Logger logger = Logger.getLogger(ProductoController.class.getName());
	
	@Autowired 
	private Environment env;
	
	@Value("${server.port}")
	private Integer port;
	@Autowired 
	private IProductoService productoService;
 
	@GetMapping("/listar")
	public List<Producto> listar(){
		logger.info("El puerto es: ${server.port}");
		return productoService.findAll().stream().map(producto -> {
			//producto.setPort(Integer.parseInt(env.getProperty("local.server.port")));
			producto.setPort(port);
			return producto;
		}).collect(Collectors.toList());		
	}
	@GetMapping("/ver/{id}")
	public Producto detalle(@PathVariable Long id) throws InterruptedException{
		/*
		if(id.equals(10L)) {
			throw new IllegalStateException("Producto no encontrado");
		}
		if(id.equals(7L)) {
			TimeUnit.SECONDS.sleep(5);
		} */
		Producto producto = productoService.findById(id);
		//producto.setPort(Integer.parseInt(env.getProperty("local.server.port")));
		producto.setPort(Integer.parseInt(env.getProperty("local.server.port")));
		/*
		try {
			Thread.sleep(2000L);
		}catch(InterruptedException e) {
			e.printStackTrace();
		} 
		*/
		return productoService.findById(id);
	}
	
	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto crear(@RequestBody Producto producto) {
		return productoService.save(producto);
	}
	
	@PutMapping("/editar/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Producto editar(@RequestBody Producto producto, @PathVariable Long id) {
		Producto productoDb = productoService.findById(id);
		productoDb.setNombre(producto.getNombre());
		productoDb.setPrecio(producto.getPrecio());
		return productoService.save(productoDb);
	}
	
	@DeleteMapping("/eliminar/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		productoService.deleteById(id);
	}
	
	
}
