package com.weijin.serialport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.weijin.serialport", exclude = SecurityAutoConfiguration.class)
public class JavaSerialPortRxtxDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaSerialPortRxtxDemoApplication.class, args);
	}
}
