package com.ssafy.cafe.controller.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.cafe.model.dto.Comment;
import com.ssafy.cafe.model.dto.Product;
import com.ssafy.cafe.model.service.ProductService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/product")
@CrossOrigin(allowCredentials = "true", originPatterns = { "*" })
public class ProductRestController {

	@Autowired
	ProductService pService;

	@GetMapping()
	@ApiOperation(value = "전체 상품의 목록을 반환한다.", response = List.class)
	public List<Product> getProductList() {
		return pService.getProductList();
	}

	@GetMapping("/{productId}")
	@ApiOperation(value = "{productId}에 해당하는 상품의 정보를 comment와 함께 반환한다."
			+ "이 기능은 상품의 comment를 조회할 때 사용된다.", response = List.class)
	public Product getProduct(@PathVariable Integer productId) {
		return pService.select(productId);
	}
	
	@PutMapping
	@Transactional
	@ApiOperation(value = "product 객체를 수정한다.", response = Integer.class)
	public Integer update(@RequestBody Product Product) {
		return pService.updateProduct(Product);
	}
}