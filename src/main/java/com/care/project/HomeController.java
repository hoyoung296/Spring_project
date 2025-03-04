package com.care.project;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	@GetMapping("/")
	public String home() {
		// ����Ʈ ���ø����̼��� Ȩ �������� �����̷�Ʈ
		return "forward:/index.html"; // React ���ø����̼��� ������ ��θ� ����
	}

}
