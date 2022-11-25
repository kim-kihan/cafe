package com.ssafy.cafe.model.dao;

import java.util.List;

import com.ssafy.cafe.model.dto.OrderDetail;

public interface OrderDetailDao {

	int insert(OrderDetail detail);

	int delete(Integer detailId);

	List<OrderDetail> select(Integer orderId);

	List<OrderDetail> selectAll();

}
