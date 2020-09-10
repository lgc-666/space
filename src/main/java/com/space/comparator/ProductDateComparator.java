package com.space.comparator;

import java.util.Comparator;

import com.space.pojo.Product;

public class ProductDateComparator implements Comparator<Product>{

	@Override
	public int compare(Product p1, Product p2) {
		return p1.getCreateDate().compareTo(p2.getCreateDate());
	}

}
