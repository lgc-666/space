package com.space.dao;
 
import com.space.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDAO extends JpaRepository<Category,Integer>{
 Category findById(int id);  //改写方法
 void deleteById(int id);
}


