package com.formacionbdi.springboot.app.kafka.service;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.formacionbdi.springboot.app.kafka.clientes.ProductoClienteRest;
import com.formacionbdi.springboot.app.kafka.models.Item;
import com.formacionbdi.springboot.app.kafka.models.Producto;

@Service("itemServiceFeign")
public class ItemServiceFeign implements ItemService{

	private static Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private ProductoClienteRest clienteFeign;
	
	@Override
	public List<Item> findAll() {
		List<Item> listItem = clienteFeign.listar().stream().map(p -> new Item(p,1)).collect(Collectors.toList());
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.kafkaTemplate.send("product-topic", objectMapper.writeValueAsString(listItem));
			log.info("JSON was sent successfully");
		}catch (JsonProcessingException e){
			log.error("JSON exception has happened.");
		}
		return listItem;
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		return new Item(clienteFeign.detalle(id),cantidad);
	}

	@Override
	public Producto save(Producto producto) {

		return clienteFeign.crear(producto);
	}

	@Override
	public Producto update(Producto producto, Long id) {
	 
		return clienteFeign.update(producto, id);
	}

	@Override
	public void delete(Long id) {
		clienteFeign.eliminar(id);		
	}


}
