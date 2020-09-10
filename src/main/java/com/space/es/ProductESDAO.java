package com.space.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.space.pojo.Product;

public interface ProductESDAO extends ElasticsearchRepository<Product,Integer>{
  void deleteById(int id);
}

