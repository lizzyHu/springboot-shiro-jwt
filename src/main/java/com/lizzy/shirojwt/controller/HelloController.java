package com.lizzy.shirojwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/say/{name}")
	public String sayHello(@PathVariable(name = "name") String name) {
		return "say " + name;
	}
	
}
