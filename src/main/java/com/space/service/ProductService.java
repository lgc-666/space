package com.space.service;

import com.space.dao.ProductDAO;
import com.space.es.ProductESDAO;
import com.space.pojo.Category;
import com.space.pojo.Product;
import com.space.util.Page4Navigator;
import com.space.util.SpringContextUtil;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames="products")
public class ProductService  {
	
	@Autowired ProductDAO productDAO;
	@Autowired
	ProductESDAO productESDAO;
	@Autowired ProductImageService productImageService;
	@Autowired CategoryService categoryService;
	@Autowired OrderItemService orderItemService;
	@Autowired ReviewService reviewService;

	@CacheEvict(allEntries=true)
	public void add(Product bean) {
		productDAO.save(bean);
		productESDAO.save(bean);
	}

	@CacheEvict(allEntries=true)
	public void delete(int id) {
		productDAO.deleteById(id);
		productESDAO.deleteById(id);
	}

	@Cacheable(key="'products-one-'+ #p0")
	public Product get(int id) {
		return productDAO.findById(id);
	}

	@CacheEvict(allEntries=true)
	public void update(Product bean) {
		productDAO.save(bean);
		productESDAO.save(bean);
	}

	@Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
	public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
    	Category category = categoryService.get(cid);
		start = start<0?0:start;
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
		Sort sort = Sort.by(order);
		PageRequest pageable = PageRequest.of(start, size, sort);
    	Page<Product> pageFromJPA =productDAO.findByCategory(category,pageable);
    	return new Page4Navigator<>(pageFromJPA,navigatePages);
	}

	public void fill(List<Category> categorys) {
		for (Category category : categorys) {
			fill(category);
		}
	}

	
	@Cacheable(key="'products-cid-'+ #p0.id")
	public List<Product> listByCategory(Category category){
		return productDAO.findByCategoryOrderById(category);
	}
	
	public void fill(Category category) {
		ProductService productService = SpringContextUtil.getBean(ProductService.class);
		List<Product> products = productService.listByCategory(category);
		productImageService.setFirstProdutImages(products);
		category.setProducts(products);
	}

	
	public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }		
	}

	
	public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        
        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);
        
	}

	
	public void setSaleAndReviewNumber(List<Product> products) {
		for (Product product : products) 
			setSaleAndReviewNumber(product);
	}

	public List<Product> search(String keyword, int start, int size) {
		initDatabase2ES();
		FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(QueryBuilders.matchPhraseQuery("name", keyword),
				ScoreFunctionBuilders.weightFactorFunction(100))
				.scoreMode(FunctionScoreQuery.ScoreMode.fromString("sum"))
				.setMinScore(10);
		start = start<0?0:start;
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
		Sort sort = Sort.by(order);
		PageRequest pageable = PageRequest.of(start, size, sort);
		SearchQuery searchQuery = new NativeSearchQueryBuilder()
				.withPageable(pageable)
				.withQuery(functionScoreQueryBuilder).build();

		Page<Product> page = productESDAO.search(searchQuery);
		return page.getContent();
	}

	private void initDatabase2ES() {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Product> page =productESDAO.findAll(pageable);
		if(page.getContent().isEmpty()) {
			List<Product> products= productDAO.findAll();
			for (Product product : products) {
				productESDAO.save(product);
			}
		}
	}

}
