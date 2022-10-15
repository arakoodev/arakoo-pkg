package com.springdemo.model;

/**
 * Simple POJO representing an Order
 * 
 * @author Shyam | catch.shyambaitmangalkar@gmail.com
 *
 */
public class Order {
	private long id;
	private String orderItem;
	private String customerName;

	public Order(long id, String orderItem, String customerName) {
		this.id = id;
		this.orderItem = orderItem;
		this.customerName = customerName;
	}

	public long getId() {
		return id;
	}

	public String getOrderItem() {
		return orderItem;
	}

	public String getCustomerName() {
		return customerName;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", orderItem=" + orderItem + ", customerName=" + customerName + "]";
	}

}
