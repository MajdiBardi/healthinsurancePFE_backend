package com.pfe.contract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.pfe.contract.clients")
public class ContractApplication {
	public static void main(String[] args) {
		SpringApplication.run(ContractApplication.class, args);
	}
}