package com.ssafy.cafe.model.dao;

import java.util.List;

import com.ssafy.cafe.model.dto.Search;

public interface SearchDao {
	
	int insert(Search search);

	List<Search> select(String userId);

}
