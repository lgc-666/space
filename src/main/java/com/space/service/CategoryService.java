package com.space.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.space.dao.CategoryDAO;
import com.space.pojo.Category;
import com.space.pojo.Product;
import com.space.util.Page4Navigator;

@Service
@CacheConfig(cacheNames="categories")  //表示 分类数据在 redis 中都放在 category 这个分组里。
public class CategoryService {
	@Autowired CategoryDAO categoryDAO;

	@CacheEvict(allEntries=true)
//	@CachePut(key="'category-one-'+ #p0")
	public void add(Category bean) {
		categoryDAO.save(bean);
	}

	@CacheEvict(allEntries=true)//删除所有缓存
//	@CacheEvict(key="'category-one-'+ #p0")
	public void delete(int id) {
		categoryDAO.deleteById(id);
	}

	
	@Cacheable(key="'categories-one-'+ #p0")  //下一次再次访问的时候，根据这个key，就可以从 redis 里取到数据了。
	public Category get(int id) {
		Category c= categoryDAO.findById(id);
		return c;
	}

	@CacheEvict(allEntries=true)
//	@CachePut(key="'category-one-'+ #p0")
	public void update(Category bean) {
		categoryDAO.save(bean);
	}

	@Cacheable(key="'categories-page-'+#p0+ '-' + #p1")
	public Page4Navigator<Category> list(int start, int size, int navigatePages) {
		start = start<0?0:start;
    	Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
    	Sort sort = Sort.by(order);
		PageRequest pageable = PageRequest.of(start, size, sort);
		Page pageFromJPA =categoryDAO.findAll(pageable);
		
		return new Page4Navigator<>(pageFromJPA,navigatePages);
	}

	@Cacheable(key="'categories-all'")
	public List<Category> list() {
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
		Sort sort = Sort.by(order);
		return categoryDAO.findAll(sort);
	}

	//这个方法的用处是删除Product对象上的 分类。 为什么要删除呢？ 因为在对分类做序列还转换为 json 的时候，会遍历里面的 products, 然后遍历出来的产品上，又会有分类，接着就开始子子孙孙无穷溃矣地遍历了，就搞死个人了
	//而在这里去掉，就没事了。 只要在前端业务上，没有通过产品获取分类的业务，去掉也没有关系
	
	public void removeCategoryFromProduct(List<Category> cs) {
		for (Category category : cs) {
			removeCategoryFromProduct(category);
		}
	}

	public void removeCategoryFromProduct(Category category) {
		List<Product> products =category.getProducts();
		if(null!=products) {
			for (Product product : products) {
				product.setCategory(null);
			}
		}
		
		List<List<Product>> productsByRow =category.getProductsByRow();
		if(null!=productsByRow) {
			for (List<Product> ps : productsByRow) {
				for (Product p: ps) {
					p.setCategory(null);
				}
			}
		}
	}
}
