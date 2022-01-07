package com.polus.tvaddtool.config;

import java.util.Properties;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.polus.tvaddtool.*", entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "transactionManager")
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class FibiRepoConfig {

	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.jpa.database-platform}")
	private String hibernateDialect;

	@Value("${spring.jpa.properties.hibernate.show_sql}")
	private String hibernateShowSql;

	@Value("${spring.jpa.properties.hibernate.hbm2ddl.auto}")
	private String hibernateHbm2ddlAuto;

	@Value("${spring.jpa.properties.hibernate.format_sql}")
	private String hibernateFormatSql;

	@Value("${spring.jpa.properties.hibernate.c3p0.minPoolSize}")
	private String hibernateMinPoolSize;

	@Value("${spring.jpa.properties.hibernate.c3p0.maxPoolSize}")
	private String hibernateMaxPoolSize;

	@Value("${spring.jpa.properties.hibernate.c3p0.timeout}")
	private String hibernateTimeOut;

	@Value("${spring.jpa.properties.hibernate.c3p0.max_statement}")
	private String hibernateMaxStmnt;

	@Value("${spring.jpa.properties.hibernate.c3p0.testConnectionOnCheckout}")
	private String hibernateTestConnectionOnCheckout;

	@Value("${spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation}")
	private String hibernateContextualCreation;

	@Value("${spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults}")
	private String hibernateSpringJDBCMetadataDefault;

	@Value("${spring.jpa.properties.hibernate.default_schema}")
	private String hibernateDefault_schema;

	@Bean
	public DataSource getDataSource() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName(driverClassName);
		driverManagerDataSource.setUrl(url);
		driverManagerDataSource.setUsername(username);
		driverManagerDataSource.setPassword(password);
		return driverManagerDataSource;
	}

	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
		hibernateTransactionManager.setDataSource(getDataSource());
		hibernateTransactionManager.setSessionFactory(sessionFactory);
		return hibernateTransactionManager;
	}

	@Bean
	@Autowired
	public HibernateTemplate getHibernateTemplate(SessionFactory sessionFactory) {
		HibernateTemplate hibernateTemplate = new HibernateTemplate(sessionFactory);
		hibernateTemplate.setCheckWriteOperations(false);
		return hibernateTemplate;
	}

	@Bean
	@Autowired
	public LocalSessionFactoryBean getSessionFactory() {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(getDataSource());
		localSessionFactoryBean.setHibernateProperties(getHibernateProperties());
		localSessionFactoryBean.setPackagesToScan(new String[] { "com.polus.tvaddtool.*" });
		// asfb.setAnnotatedClasses(new Class<?>[]{PrincipalBo.class});
		return localSessionFactoryBean;
	}

	@Bean
	public Properties getHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", hibernateDialect);
		properties.put("hibernate.show_sql", hibernateShowSql);
//		properties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
		properties.put("hibernate.format_sql", hibernateFormatSql);

		properties.put("hibernate.c3p0.minPoolSize", hibernateMinPoolSize);
		properties.put("hibernate.c3p0.maxPoolSize", hibernateMaxPoolSize);
		properties.put("hibernate.c3p0.timeout", hibernateTimeOut);
		properties.put("hibernate.c3p0.max_statement", hibernateMaxStmnt);
		properties.put("hibernate.c3p0.testConnectionOnCheckout", hibernateTestConnectionOnCheckout);
		// properties.put("hibernate.temp.use_jdbc_metadata_defaults", hibernateMetadataDefaults);
		properties.put("hibernate.jdbc.lob.non_contextual_creation", hibernateContextualCreation);
		properties.put("spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults", hibernateSpringJDBCMetadataDefault);
		properties.put("hibernate.default_schema", hibernateDefault_schema);
		return properties;
	}

	@Primary
	@Bean
	@Autowired
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(getDataSource());
		factoryBean.setPackagesToScan(new String[] { "com.polus.tvaddtool.*" });
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
		factoryBean.setJpaProperties(getHibernateProperties());
		return factoryBean;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		return hibernateJpaVendorAdapter;
	}

}
