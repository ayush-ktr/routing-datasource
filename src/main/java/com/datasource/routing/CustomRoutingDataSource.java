package com.datasource.routing;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class CustomRoutingDataSource extends AbstractRoutingDataSource {

	private static final Logger LOG = LoggerFactory.getLogger(CustomRoutingDataSource.class);
	private static final ThreadLocal<String> DATASOURCE = new ThreadLocal<>();

	@Override
	protected Object determineCurrentLookupKey() {
		return DATASOURCE.get();
	}

	public CustomRoutingDataSource(DataSource dataSource, Map<Object, Object> targetDataSources) {
		LOG.info("Initializing CustomRouting datasource");
		targetDataSources.put("-1", dataSource);
		this.setTargetDataSources(targetDataSources);
		this.setDefaultTargetDataSource(dataSource);
		this.initialize();
	}

	public static ThreadLocal<String> getDatasource() {
		return DATASOURCE;
	}

	public static void setDatasource(String datasourceKey) {
		LOG.info("Setting datasource :: " + datasourceKey);
		DATASOURCE.set(datasourceKey);
	}

}
