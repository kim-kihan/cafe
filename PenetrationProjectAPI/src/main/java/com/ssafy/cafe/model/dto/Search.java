package com.ssafy.cafe.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Search {

	private Integer id;
	private String userId;
	private String content;

	@Builder
	public Search(Integer id, String userId, String content) {
		super();
		this.id = id;
		this.userId = userId;
		this.content = content;
	}

	public Search(String userId, String content) {
		this.userId = userId;
		this.content = content;
	}
}
