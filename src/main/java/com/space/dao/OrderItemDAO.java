package com.space.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.pojo.Order;
import com.space.pojo.OrderItem;
import com.space.pojo.Product;
import com.space.pojo.User;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
	List<OrderItem> findByOrderOrderByIdDesc(Order order);
	List<OrderItem> findByProduct(Product product);
	List<OrderItem> findByUserAndOrderIsNull(User user);
	OrderItem findById(int id);
	void deleteById(int id);
}
