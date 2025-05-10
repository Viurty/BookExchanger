package com.example.bookservice.config;

import com.example.bookservice.auth.AuthServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

  @Value("${grpc.auth-service-host:localhost}")
  private String host;

  @Value("${grpc.auth-service-port}")
  private int port;

  @Value("${grpc.auth-service-secret}")
  private String secret;

  @Bean(destroyMethod = "shutdown")
  public ManagedChannel authManagedChannel() {
    return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
  }

  @Bean
  public AuthServiceGrpc.AuthServiceBlockingStub authServiceStub(
      ManagedChannel authManagedChannel) {
    return AuthServiceGrpc.newBlockingStub(authManagedChannel);
  }
}
