package com.meetProject.signalserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class SignalserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignalserverApplication.class, args);
	}

}
