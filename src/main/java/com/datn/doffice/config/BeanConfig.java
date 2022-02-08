package com.datn.doffice.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;


@Configuration
public class BeanConfig {

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:application.properties", "classpath:/error/error");
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

//	@Bean
//	public LocalValidatorFactoryBean validator() {
//		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
//		bean.setValidationMessageSource(messageSource());
//		return bean;
//	}

//	@Bean
//	public freemarker.template.Configuration freemarkerConfig() {
//		freemarker.template.Configuration config = new freemarker.template.Configuration(freemarker.template.Configuration.getVersion());
//		config.setClassForTemplateLoading(this.getClass(), "/templates/");
//		return config;
//	}

}
