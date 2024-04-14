package com.datasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication( exclude = {DataSourceAutoConfiguration.class})
public class RoutingDatasourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoutingDatasourceApplication.class, args);
	}

}
