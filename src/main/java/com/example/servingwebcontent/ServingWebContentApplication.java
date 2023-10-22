package com.example.servingwebcontent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SpringBootApplication
@EnableWebSocket
@EnableScheduling
public class ServingWebContentApplication  {

    @Bean
	public ServletRegistrationBean<ThymeleafServlet> thymeleafServletBean() {
		ServletRegistrationBean<ThymeleafServlet> bean = new ServletRegistrationBean<>(new ThymeleafServlet(), "/thymeleafServlet/*");
		bean.setLoadOnStartup(1);
		return bean;
	}

	@Bean
	public ServletRegistrationBean<Example> exampleServletBean() {
		ServletRegistrationBean<Example> bean = new ServletRegistrationBean<>(new Example(), "/exampleServlet/*");
		bean.setLoadOnStartup(1);
		return bean;
	}
    
    public static void main(String[] args) {

        SpringApplication.run(ServingWebContentApplication.class, args);
    }


}