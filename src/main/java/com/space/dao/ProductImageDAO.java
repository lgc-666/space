package com.space.dao;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.pojo.Product;
import com.space.pojo.ProductImage;

public interface ProductImageDAO extends JpaRepository<ProductImage,Integer>{
	public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);
	ProductImage findById(int id);
	void deleteById(int id);
}
