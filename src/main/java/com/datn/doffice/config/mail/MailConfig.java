package com.datn.doffice.config.mail;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableCaching
@NoArgsConstructor
public class MailConfig {

  @Value("${spring.mail.default-encoding}")
  private String defaultEncoding;

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.username}")
  private String userName;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.port}")
  private int port;

  @Value("${spring.mail.protocol}")
  private String protocol;

  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  private boolean starttlsEnable;

  @Value("${spring.mail.properties.mail.smtp.ssl.enable}")
  private boolean sslEnable;

  @Value("${spring.mail.test-connection}")
  private boolean testConnection;

  @Value("${spring.mail.login.path}")
  private String loginPath;

  private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

  private static final String TEST_CONNETION = "mail.test-connection";

  @Bean(name = "mailPath")
  public Map<String, String> mailPath() {
    Map<String, String> mapA = new HashMap<>();
    mapA.put("LOGIN_PATH", loginPath);
    return mapA;
  }

  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setProtocol(protocol);
    mailSender.setUsername(userName);
    mailSender.setPassword(password);

    mailSender.setDefaultEncoding(defaultEncoding);
    Properties properties = mailSender.getJavaMailProperties();
    properties.put(MAIL_SMTP_STARTTLS_ENABLE, String.valueOf(starttlsEnable));
    if(sslEnable){
      properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      properties.put("mail.smtp.ssl.enable", "true");
      properties.put("mail.smtp.auth", "true");

    }
    properties.put(TEST_CONNETION, String.valueOf(testConnection));

    mailSender.setJavaMailProperties(properties);

    return mailSender;
  }
}
