package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FilmorateApplication {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		start();
	}

	public static void start() {
		context = SpringApplication.run(FilmorateApplication.class);
	}

	public static void stop() {
		if (context != null) {
			SpringApplication.exit(context);
		}
	}

}
