package com.datn.doffice;

import com.datn.doffice.utils.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableConfigurationProperties({FileStorageProperties.class})
public class DigitalOfficeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalOfficeBackendApplication.class, args);
	}

}
