package com.acme.intreportapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * This class is used to resolve the template configuration
 * 
 * @author SAP 
 *
 */
@Configuration
public class ThymeleafConfig {

	private static final String VIEW_TYPE = ".html";
	private static final String VIEW_TEMPLATE_MODE = "HTML5";
	private static final String VIEW_TEMPLATES_PATH = "/WEB-INF/views/";

	@Bean
	public ServletContextTemplateResolver templateResolver() {
		ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
		resolver.setPrefix(VIEW_TEMPLATES_PATH);
		resolver.setSuffix(VIEW_TYPE);
		resolver.setTemplateMode(VIEW_TEMPLATE_MODE);
		resolver.setOrder(1);
		return resolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		engine.addDialect(new LayoutDialect());
		return engine;
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		return resolver;
	}
}
