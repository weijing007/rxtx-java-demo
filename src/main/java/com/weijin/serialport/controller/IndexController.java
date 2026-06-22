package com.weijin.serialport.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.weijin.serialport.jSerialComm.SerialCommPortService;

@Controller
public class IndexController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SerialCommPortService serialCommPortService;

	@GetMapping(value = "/")
	public String index(Model model) {
		List<String> ports = serialCommPortService.findPorts();
		model.addAttribute("ports", ports);
		return "index";
	}

	@GetMapping(value = "/home")
	public String home(Model model) {
		List<String> ports = serialCommPortService.findPorts();
		model.addAttribute("ports", ports);
		return "home";
	}
}
