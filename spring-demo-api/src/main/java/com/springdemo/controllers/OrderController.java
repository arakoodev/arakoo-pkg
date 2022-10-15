package com.springdemo.controllers;

import java.util.List;

import com.springdemo.dao.OrderDataSource;
import com.springdemo.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
/**
 * 
 * @author Shyam | catch.shyambaitmangalkar@gmail.com
 *
 */
@RestController
public class OrderController {
	@Autowired
	private OrderDataSource dataSource;
	
//	@RequestMapping(value = "/order/{id}", method = RequestMethod.GET)
//	public Order getOrder(@PathVariable long id) {
//		return dataSource.getOrder(id);
//	}
	
	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	public List<Order> getAllOrders() {
		return dataSource.getAllOrders();
	}
}
