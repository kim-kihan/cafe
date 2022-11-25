package com.ssafy.cafe.model.service;

import java.util.List;

import com.ssafy.cafe.model.dto.Search;
import com.ssafy.cafe.model.dto.Stamp;

public interface SearchService {

	List<Search> select(String userId);

	int insert(Search search);
}
