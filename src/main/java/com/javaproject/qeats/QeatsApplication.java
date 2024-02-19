package com.javaproject.qeats;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class QeatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(QeatsApplication.class, args);
	}

	/**
	 * Fetches a ModelMapper instance.
	 *
	 * @return ModelMapper
	 */
	@Bean // Want a new obj every time
	@Scope("prototype")
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
