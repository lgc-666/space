package com.space.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.space.dao.UserDAO;
import com.space.pojo.User;
import com.space.util.Page4Navigator;
import com.space.util.SpringContextUtil;

@Service
@CacheConfig(cacheNames="users")
public class UserService {
	@Autowired UserDAO userDAO;

	public boolean isExist(String name) {
		UserService userService = SpringContextUtil.getBean(UserService.class);
		User user = userService.getByName(name);
		return null!=user;
	}

	@Cacheable(key="'users-one-name-'+ #p0")
	public User getByName(String name) {
		return userDAO.findByName(name);
	}
	
	

	@Cacheable(key="'users-one-name-'+ #p0 +'-password-'+ #p1")
	public User get(String name, String password) {
		return userDAO.getByNameAndPassword(name,password);
	}

	@Cacheable(key="'users-page-'+#p0+ '-' + #p1")
	public Page4Navigator<User> list(int start, int size, int navigatePages) {
		start = start<0?0:start;
		Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
		Sort sort = Sort.by(order);
		PageRequest pageable = PageRequest.of(start, size, sort);

		Page pageFromJPA =userDAO.findAll(pageable);
		return new Page4Navigator<>(pageFromJPA,navigatePages);
	}

	@CacheEvict(allEntries=true)
	public void add(User user) {
		userDAO.save(user);
	}
}
