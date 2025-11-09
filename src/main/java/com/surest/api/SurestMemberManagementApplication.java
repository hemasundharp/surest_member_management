package com.surest.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
@EnableCaching  // Enable caching globally
public class SurestMemberManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurestMemberManagementApplication.class, args);
	}

}
