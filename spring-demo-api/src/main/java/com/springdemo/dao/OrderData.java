package com.springdemo.dao;

import com.springdemo.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * As there is no database hooked up at the backend, this
 * class acts as a database by providing a <code>List</code>
 * containing <code>Order</code>(s).
 *  
 * @author Shyam | catch.shyambaitmangalkar@gmail.com
 *
 */
public class OrderData {
	private static List<Order> orders;
	
	private OrderData() {
		
	}
	
	public static List<Order> getOrderData() {
		if(orders == null) {
			orders = new ArrayList<>();
			orders.add(new Order(1, "Laptop", "Shyam"));
			orders.add(new Order(2, "Hammer", "Thor"));
			orders.add(new Order(3, "Arc Reactor", "Tony Stark"));
			orders.add(new Order(4, "Shield", "Steve Rogers"));
			orders.add(new Order(5, "Beer", "Bruce Banner"));
			orders.add(new Order(6, "Glock", "Natasha Romanoff"));
			orders.add(new Order(7, "Coke", "Clint Barton"));
			orders.add(new Order(8, "Shoes", "Sam Wilson"));
			orders.add(new Order(9, "Jacket", "Bucky Barns"));
			orders.add(new Order(10, "Popcorn", "Vision"));
		}
		return orders;
	}
}
