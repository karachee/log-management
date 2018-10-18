package com.karachee.lms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.karachee.lms.repository.base.ExtendedCrudRepositoryImpl;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.HashMap;
import java.util.TimeZone;

@Configuration
@EnableWebMvc
@PropertySource(value = {"classpath:logManagementService.properties"})
@ComponentScan(basePackages = "com.karachee.lms")
@EnableJpaRepositories(basePackages = "com.karachee.lms.repository", repositoryBaseClass = ExtendedCrudRepositoryImpl.class)
public class ApiConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Bean
    public Logger logger(InjectionPoint injectionPoint) {
        Class<?> targetClass = injectionPoint.getMember().getDeclaringClass();
        return LogManager.getLogger(targetClass);
    }

    @Bean(name = "objectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper() {
            private static final long serialVersionUID = 1L;
            {
                registerModule(new JodaModule());
                setSerializationInclusion(JsonInclude.Include.NON_NULL);
                configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                setTimeZone(TimeZone.getTimeZone("America/Denver"));
            }
        };
    }

    @Bean(name = "prettyObjectMapper")
    public ObjectMapper prettyObjectMapper() {
        return new ObjectMapper() {
            private static final long serialVersionUID = 1L;

            {
                registerModule(new JodaModule());
                enable(SerializationFeature.INDENT_OUTPUT);
                configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                setTimeZone(TimeZone.getTimeZone("America/Denver"));
            }
        };
    }

    @Bean(name = "xmlMapper")
    public ObjectMapper xmlMapper() {
        return new XmlMapper();
    }

    @Bean(name = "prettyXmlMapper")
    public ObjectMapper prettyXmlMapper() {
        return new XmlMapper() {
            private static final long serialVersionUID = 1L;

            {
                enable(SerializationFeature.INDENT_OUTPUT);
            }
        };
    }

    @Bean
    public ComboPooledDataSource dataSource() throws Exception {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass("org.postgresql.Driver");
        comboPooledDataSource.setJdbcUrl(environment.getProperty("log.jdbcUrl"));
        comboPooledDataSource.setUser(environment.getProperty("log.user"));
        comboPooledDataSource.setPassword(environment.getProperty("log.password"));
        comboPooledDataSource.setTestConnectionOnCheckout(environment.getProperty("log.testConnectionOnCheckout", Boolean.class));
        comboPooledDataSource.setInitialPoolSize(environment.getProperty("log.initialPoolSize", Integer.class));
        comboPooledDataSource.setMinPoolSize(environment.getProperty("log.minPoolSize", Integer.class));
        comboPooledDataSource.setMaxPoolSize(environment.getProperty("log.maxPoolSize", Integer.class));
        comboPooledDataSource.setAcquireIncrement(environment.getProperty("log.acquireIncrement", Integer.class));
        comboPooledDataSource.setMaxStatementsPerConnection(environment.getProperty("log.maxStatementsPerConnection", Integer.class));
        comboPooledDataSource.setAcquireRetryAttempts(environment.getProperty("log.acquireRetryAttempts", Integer.class));
        comboPooledDataSource.setAcquireRetryDelay(environment.getProperty("log.acquireRetryDelay", Integer.class));
        comboPooledDataSource.setMaxIdleTime(environment.getProperty("log.maxIdleTime", Integer.class));
        comboPooledDataSource.setMaxConnectionAge(environment.getProperty("log.maxConnectionAge", Integer.class));
        comboPooledDataSource.setPreferredTestQuery(environment.getProperty("log.preferredTestQuery"));
        comboPooledDataSource.setCheckoutTimeout(environment.getProperty("log.checkoutTimeout", Integer.class));
        return comboPooledDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateJpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("com.karachee.lms.models");
        emf.setJpaVendorAdapter(hibernateJpaVendorAdapter());
        emf.setJpaPropertyMap(new HashMap<String, Object>(){{
            put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            put("hibernate.cache.use_second_level_cache", false);
            put("hibernate.cache.use_query_cache", true);
            put("hibernate.generate_statistics", true);
            put("hibernate.session_factory_name", "log");
            put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
            put("hibernate.cache.use_structured_entries", false);
            put("hibernate.show_sql", true);
            put("hibernate.format_sql", true);
            put("hibernate.enable_lazy_load_no_trans", true);
        }});
        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager() throws Exception {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        jpaTransactionManager.setJpaDialect(new HibernateJpaDialect());
        return jpaTransactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
