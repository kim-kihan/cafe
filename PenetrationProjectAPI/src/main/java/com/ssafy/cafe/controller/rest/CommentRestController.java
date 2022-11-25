package com.ssafy.cafe.controller.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.cafe.model.dto.Comment;
import com.ssafy.cafe.model.service.CommentService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/comment")
@CrossOrigin(allowCredentials = "true", originPatterns = { "*" })
public class CommentRestController {

	@Autowired
	CommentService cService;

	@PostMapping
	@Transactional
	@ApiOperation(value = "comment 객체를 추가한다.", response = Boolean.class)
	public Boolean insert(@RequestBody Comment comment) {
		cService.addComment(comment);
		return true;
	}
	
	@GetMapping("/{productId}")
	@ApiOperation(value = "{productId}에 해당하는 comment 리스트를 불러온다.", response = List.class)
	public List<Comment> selectByProduct(@PathVariable Integer productId) {
		return cService.selectByProduct(productId);
	}
	
	@GetMapping()
	@ApiOperation(value = "모든 comment 리스트를 불러온다.", response = List.class)
	public List<Comment> selectAll() {
		return cService.selectAll();
	}

	@DeleteMapping("/{id}")
	@Transactional
	@ApiOperation(value = "{id}에 해당하는 사용자 정보를 삭제한다.", response = Boolean.class)
	public Boolean delete(@PathVariable Integer id) {
		cService.removeComment(id);
		return true;
	}

	@PutMapping
	@Transactional
	@ApiOperation(value = "comment 객체를 수정한다.", response = Boolean.class)
	public Boolean update(@RequestBody Comment comment) {
		cService.updateComment(comment);
		return true;
	}
}