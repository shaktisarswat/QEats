package com.javaproject.qeats;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
@ComponentScan(basePackages = {"com.javaproject.qeats"})

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
