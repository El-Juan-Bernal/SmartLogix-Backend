package com.jaba.bff_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- Importante

@SpringBootApplication
@EnableFeignClients // <-- Importante
public class BffWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffWebApplication.class, args);
	}

}


