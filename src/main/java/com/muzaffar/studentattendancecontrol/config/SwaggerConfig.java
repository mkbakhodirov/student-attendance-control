package com.muzaffar.studentattendancecontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Student Attendance Control REST API",
                "API for project of controlling attendance of students in a university",
                "API TOS",
                "Terms of service",
                new Contact("Muzaffar Bakhodirov", "https://www.linkedin.com/in/mkbakhodirov/",
                        "mkbakhodirov@gmail.com"),
                "License of API", "API license URL", Collections.emptyList()
        );
    }
}
