package com.ssafy.cafe.controller.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.cafe.model.dto.Search;
import com.ssafy.cafe.model.service.SearchService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/search")
@CrossOrigin(allowCredentials = "true", originPatterns = { "*" })
public class SearchRestController {
	@Autowired
	SearchService sService;


	@PostMapping
	@ApiOperation(value = "사용자 최근검색어를 추가한다.", response = Boolean.class)
	public Integer insert(@RequestBody Search search) {
		return sService.insert(search);
	}

	@GetMapping("/{userId}")
	@ApiOperation(value = "userId에 해당하는 검색어를 불러온다.", response = Boolean.class)
	public List<Search> select(@PathVariable String userId) {
		return sService.select(userId);
	}
}
