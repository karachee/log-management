package com.karachee.lms.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Component
public class SwaggerConfiguration extends WebMvcConfigurerAdapter {

    @Value("${swagger.path}")
    String path;

    @Value("${swagger.title}")
    String title;

    @Value("${swagger.description}")
    String description;

    @Value("${swagger.tos.url}")
    String tosUrl;

    @Value("${swagger.contact}")
    String contact;

    @Value("${swagger.license}")
    String license;

    @Value("${swagger.license.url}")
    String licenseUrl;

    @Value("${swagger.api.version}")
    String api;

    @Value("${version:unknown}")
    private String apiVersion;

    @Bean
    public Docket serviceRegistryApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).useDefaultResponseMessages(false).select().build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(title).description(description).termsOfServiceUrl(tosUrl).license(license)
                .licenseUrl(licenseUrl).version(apiVersion).build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
