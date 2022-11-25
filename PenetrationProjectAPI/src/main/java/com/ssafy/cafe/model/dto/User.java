package com.ssafy.cafe.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

	private String id;
	private String name;
	private String pass;
	private Integer stamps;
	private String age;
	private String gender;
	//private List<Stamp> stampList = new ArrayList<>();


	@Builder
	public User(String id, String name, String pass, Integer stamps) {
		super();
		this.id = id;
		this.name = name;
		this.pass = pass;
		this.stamps = stamps;
	}

	public User(String id, String name, String pass) {
		super();
		this.id = id;
		this.name = name;
		this.pass = pass;
	}
	
	@Builder
	public User(String id, String name, String pass, Integer stamps, String age, String gender) {
		super();
		this.id = id;
		this.name = name;
		this.pass = pass;
		this.stamps = stamps;
		this.age = age;
		this.gender = gender;
	}

	public User(String id, String name, String pass, String age, String gender) {
		super();
		this.id = id;
		this.name = name;
		this.pass = pass;
		this.age = age;
		this.gender = gender;
	}
}
