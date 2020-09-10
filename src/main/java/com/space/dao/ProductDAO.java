package com.space.dao;

import com.space.pojo.Category;
import com.space.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDAO extends JpaRepository<Product,Integer>{
	Page<Product> findByCategory(Category category, Pageable pageable);
	List<Product> findByCategoryOrderById(Category category);
	List<Product> findByNameLike(String keyword, Pageable pageable);
    void deleteById(int id);
	Product findById(int id);
}

