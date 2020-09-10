package com.space.dao;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.pojo.Order;
import com.space.pojo.User;

public interface OrderDAO extends JpaRepository<Order,Integer>{
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
    Order findById(int oid);
}


