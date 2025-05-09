package com.example.bookservice;

import com.example.bookservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Bean
  public UserService userService() {
    return new UserService(8081);
  }
}
