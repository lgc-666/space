package com.space.dao;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.space.pojo.Product;
import com.space.pojo.Review;

public interface ReviewDAO extends JpaRepository<Review,Integer>{

	List<Review> findByProductOrderByIdDesc(Product product);
	int countByProduct(Product product);

}
