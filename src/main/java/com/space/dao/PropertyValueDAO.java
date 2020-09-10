package com.space.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.pojo.Product;
import com.space.pojo.Property;
import com.space.pojo.PropertyValue;

public interface PropertyValueDAO extends JpaRepository<PropertyValue,Integer>{

	List<PropertyValue> findByProductOrderByIdDesc(Product product);
	PropertyValue getByPropertyAndProduct(Property property, Product product);

	
	
	
}
