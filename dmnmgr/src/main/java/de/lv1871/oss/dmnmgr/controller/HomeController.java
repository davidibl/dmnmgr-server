package de.lv1871.oss.dmnmgr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/api")
	public String home() {
		return "redirect:swagger-ui.html";
	}

}
