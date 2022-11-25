package com.ssafy.cafe.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.cafe.model.dao.SearchDao;
import com.ssafy.cafe.model.dto.Search;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	SearchDao sDao;
	
	@Override
	public List<Search> select(String userId) {
		// TODO Auto-generated method stub
		return sDao.select(userId);
	}

	@Override
	public int insert(Search search) {
		// TODO Auto-generated method stub
		return sDao.insert(search);
	}

}
