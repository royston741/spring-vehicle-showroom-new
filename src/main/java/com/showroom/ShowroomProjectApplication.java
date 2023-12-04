package com.showroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EntityScan(basePackages = "com.showroom.entity")
@EnableTransactionManagement
@SpringBootApplication
public class ShowroomProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowroomProjectApplication.class, args);
		System.out.println("Running.....");

	}

}
