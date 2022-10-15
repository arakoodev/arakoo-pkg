package com.springdemo.dao;

import java.util.List;

import com.springdemo.model.Order;

/**
 * DataSource
 * 
 * @author Shyam | catch.shyambaitmangalkar@gmail.com
 *
 */
public interface OrderDataSource {
	Order getOrder(long id);
	List<Order> getAllOrders();
}
