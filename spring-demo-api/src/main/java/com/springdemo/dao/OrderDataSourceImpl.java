package com.springdemo.dao;

import java.util.List;

import com.springdemo.model.Order;
import org.springframework.stereotype.Component;

/**
 * DataSource implementation
 * 
 * @author Shyam | catch.shyambaitmangalkar@gmail.com
 *
 */
@Component
public class OrderDataSourceImpl implements OrderDataSource {
	private List<Order> orders = OrderData.getOrderData();

	@Override
	public Order getOrder(long id) {
		return orders.stream().filter(o -> o.getId() == id).findAny().get();
	}

	@Override
	public List<Order> getAllOrders() {
		return orders;
	}

}
