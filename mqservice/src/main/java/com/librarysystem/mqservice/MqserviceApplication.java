package com.librarysystem.mqservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
		"com.librarysystem.mqservice",
		"com.librarysystem.commonservice"
})
public class MqserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MqserviceApplication.class, args);
	}

}
