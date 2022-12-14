package com.ssafy.cafe.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.cafe.model.dao.ProductDao;
import com.ssafy.cafe.model.dto.Comment;
import com.ssafy.cafe.model.dto.Product;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDao pDao;

	@Override
	public List<Product> getProductList() {
		return pDao.selectAll();
	}

	@Override
	public Product select(Integer productId) {
		return pDao.select(productId);
	}

	@Override
	public int updateProduct(Product product) {
		// TODO Auto-generated method stub
		return pDao.update(product);
	}

}
