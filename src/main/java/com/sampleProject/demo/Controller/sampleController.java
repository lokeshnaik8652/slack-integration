package com.sampleProject.demo.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class sampleController {

	
	@GetMapping("/samplea-api")
	public String samplemethod() {
		return "Hello lokesh sample";
	}
}
