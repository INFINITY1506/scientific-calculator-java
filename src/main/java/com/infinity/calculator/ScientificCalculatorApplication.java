package com.infinity.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ScientificCalculatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScientificCalculatorApplication.class, args);
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> serverPortCustomizer(
            @Value("${PORT:8080}") int port,
            @Value("${BIND_ADDRESS:0.0.0.0}") String bind) {
        return factory -> {
            try {
                factory.setAddress(java.net.InetAddress.getByName(bind));
            } catch (Exception ignored) {}
            factory.setPort(port);
        };
    }

    @Bean
    public WebMvcConfigurer forwardIndex() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("index");
            }
        };
    }
}


