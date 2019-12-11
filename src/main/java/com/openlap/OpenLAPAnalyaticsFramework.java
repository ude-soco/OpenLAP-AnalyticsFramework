package com.openlap;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * Main Spring Application of the OpenLAP-Core
 */
@SpringBootApplication
//@Configuration
//@PropertySource("classpath:/application.properties")
//@ComponentScan(basePackageClasses = { OpenLAPAnalyaticsFramework.class })

public class OpenLAPAnalyaticsFramework {


    public static String API_VERSION_NUMBER;

    /**
     * Start the application
     *
     * @paramargs
     */
    public static void main(String[] args) {
        SpringApplication.run(OpenLAPAnalyaticsFramework.class, args);
    }
    /*@Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }*/

}
