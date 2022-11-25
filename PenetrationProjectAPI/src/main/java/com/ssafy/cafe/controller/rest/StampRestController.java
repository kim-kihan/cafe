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

import com.ssafy.cafe.model.dto.Order;
import com.ssafy.cafe.model.dto.Stamp;
import com.ssafy.cafe.model.dto.User;
import com.ssafy.cafe.model.service.OrderService;
import com.ssafy.cafe.model.service.StampService;
import com.ssafy.cafe.model.service.UserService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/stamp")
@CrossOrigin(allowCredentials = "true", originPatterns = { "*" })
public class StampRestController {
	@Autowired
	StampService sService;


	@PostMapping
	@ApiOperation(value = "사용자 정보를 추가한다.", response = Boolean.class)
	public Integer insert(@RequestBody Stamp stamp) { // (#{userId}, #{orderId}, #{quantity})
		return sService.insert(stamp);
	}

	@GetMapping("/{userId}")
	@ApiOperation(value = "userId에 해당하는 stamp를 불러온다.", response = Boolean.class)
	public List<Stamp> selectByUserId(@PathVariable String userId) {
		return sService.selectByUser(userId);
	}

	public Map<String, Object> getGrade(Integer stamp) {
		Map<String, Object> grade = new HashMap<>();
		int pre = 0;
		for (Level level : levels) {
			if (level.max >= stamp) {
				grade.put("title", level.title);
				grade.put("img", level.img);
				if (!level.title.equals("커피나무")) {
					int step = (stamp - pre) / level.unit + ((stamp - pre) % level.unit > 0 ? 1 : 0);
					grade.put("step", step);
					int to = level.unit - (stamp - pre) % level.unit;
					grade.put("to", to);
				}
				break;
			}
			else {
				pre = level.max;
			}
		}
		return grade;
	}

	private List<Level> levels;

	@PostConstruct
	public void setup() {
		levels = new ArrayList<>();
		levels.add(new Level("씨앗", 10, 50, "seeds.png"));
		levels.add(new Level("꽃", 15, 125, "flower.png"));
		levels.add(new Level("열매", 20, 225, "coffee_fruit.png"));
		levels.add(new Level("커피콩", 25, 350, "coffee_beans.png"));
		levels.add(new Level("커피나무", Integer.MAX_VALUE, Integer.MAX_VALUE, "coffee_tree.png"));
	}

	class Level {
		private String title;
		private int unit;
		private int max;
		private String img;

		private Level(String title, int unit, int max, String img) {
			this.title = title;
			this.unit = unit;
			this.max = max;
			this.img = img;
		}
	}
}
