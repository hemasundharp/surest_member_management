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
		loadDotenv();
		SpringApplication.run(SurestMemberManagementApplication.class, args);
	}
	private static void loadDotenv() {
		try {
			FileInputStream input = new FileInputStream(".env");
			Properties prop = new Properties();
			prop.load(input);
			input.close();
			

			for (String name : prop.stringPropertyNames()) {
				String value = prop.getProperty(name);
				System.setProperty(name, value);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
