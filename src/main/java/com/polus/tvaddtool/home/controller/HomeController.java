package com.polus.tvaddtool.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping(value = "/")
	public String getLangingpage(Model model) {
		return "index";
	}

	@GetMapping("/success")
	public String sucess(Model model) {
		return "success";
	}

}
