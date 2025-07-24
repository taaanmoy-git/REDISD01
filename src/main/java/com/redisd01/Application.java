package com.redisd01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // ✅ Important for Redis caching to work
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
