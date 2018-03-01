package com.mk.recitalplayer.RecitalPlayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories
public class RecitalPlayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecitalPlayerApplication.class, args);
	}
}
