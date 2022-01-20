package com.example.encuentro2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class Encuentro2Application {

	public static void main(String[] args) {
		SpringApplication.run(Encuentro2Application.class, args);
	}

}
