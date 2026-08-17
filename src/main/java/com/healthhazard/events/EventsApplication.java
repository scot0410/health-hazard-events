package com.healthhazard.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventsApplication {

	public static void main(String[] args) {
		var appContext = SpringApplication.run(EventsApplication.class, args);
		System.exit(SpringApplication.exit(appContext));
	}

}
