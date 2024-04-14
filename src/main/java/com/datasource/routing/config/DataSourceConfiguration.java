package com.datasource.routing.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.datasource.routing.CustomRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.datasource.repository", entityManagerFactoryRef = "entityManagerFactoryBean", transactionManagerRef = "transcationManager")
public class DataSourceConfiguration {

	private final Logger LOG = LoggerFactory.getLogger(DataSourceConfiguration.class);

	@Autowired
	Environment env;

	@Bean(name = "dbConfigDataSource")
	public DataSource getDBConfigDataSource() {
		//DriverManagerDataSource
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(env.getProperty("config.datasource.url"));
		hikariConfig.setUsername(env.getProperty("config.datasource.username"));
		hikariConfig.setPassword(env.getProperty("config.datasource.password"));
		LOG.info("DB Config Datasource created");
		return new HikariDataSource(hikariConfig);
	}

	@Bean(name = "defaultDataSource")
	public DataSource getDefaultDataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(env.getProperty("default.datasource.url"));
		hikariConfig.setUsername(env.getProperty("default.datasource.username"));
		hikariConfig.setPassword(env.getProperty("default.datasource.password"));
		LOG.info("Default Datasource created");
		return new HikariDataSource(hikariConfig);
	}

	private Map<Object, Object> loadDataSources() {
		Map<Object, Object> dataSourceMap = new HashMap<>();
		try (Connection con = getDBConfigDataSource().getConnection()) {
			String selectQuery = "SELECT datasource_name, account_id, url, username, password FROM "
					+ "map_account_datasource AS mad INNER JOIN lu_datasource AS ld ON "
					+ "mad.fk_datasource_id = ld.pk_datasource_id WHERE ld.is_deleted = 0";
			LOG.info("Datasource loading started");
			ResultSet rs = con.prepareStatement(selectQuery).executeQuery();
			while (rs.next()) {
				LOG.info("DataSource creation for : " + rs.getString("account_id"));
				DataSource dataSource = prepareDataSource(rs);
				dataSourceMap.put(rs.getString("account_id"), dataSource);
			}
			LOG.info("Datasource loading done");
		} catch (Exception e) {
			LOG.error("Exception wile setting initializing datasources: ", e);
		}
		return dataSourceMap;
	}

	private DataSource prepareDataSource(ResultSet rs) throws Exception {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(rs.getString("url"));
		hikariConfig.setUsername(rs.getString("username"));
		hikariConfig.setPassword(rs.getString("password"));
		hikariConfig.setPoolName(rs.getString("datasource_name") + "-pool");
		LOG.info(hikariConfig.toString());
		return new HikariDataSource(hikariConfig);
	}

	@Bean(name = "entityManagerFactoryBean")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

		Map<Object, Object> dataSourceMap = loadDataSources();
		CustomRoutingDataSource customRoutingDataSource = new CustomRoutingDataSource(getDefaultDataSource(),
				dataSourceMap);

		localContainerEntityManagerFactoryBean.setDataSource(customRoutingDataSource);
		localContainerEntityManagerFactoryBean.setPackagesToScan("com.datasource.entity");
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		localContainerEntityManagerFactoryBean.setJpaPropertyMap(
				Map.of("hibernate.dialect", "org.hibernate.dialect.MySQLDialect", "hibernate.show_sql", "true"));

		return localContainerEntityManagerFactoryBean;
	}

	@Bean(name = "transcationManager")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager manager = new JpaTransactionManager();
		manager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return manager;
	}

}
