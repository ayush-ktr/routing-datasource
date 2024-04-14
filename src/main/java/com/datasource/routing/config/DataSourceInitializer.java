package com.datasource.routing.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

@Component
public class DataSourceInitializer implements ApplicationRunner {
	
	private final Logger LOG = LoggerFactory.getLogger(DataSourceInitializer.class);

	
	@Autowired
	DataSource dbConfigDataSource;

	@Override
	public void run(ApplicationArguments args) throws Exception {
//		loadDataSources();
	}
	
	
	

}
