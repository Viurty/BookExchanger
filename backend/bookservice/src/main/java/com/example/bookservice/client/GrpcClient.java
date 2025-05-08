package com.example.bookservice.client;

import com.example.bookservice.auth.AuthServiceGrpc;
import com.example.bookservice.auth.AuthServiceGrpc.AuthServiceBlockingStub;
import com.example.bookservice.auth.LoginRequest;
import com.example.bookservice.auth.RoleStatus;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
  public static void main(String[] args) {
    // Создаем канал и stub
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 8081).usePlaintext().build();
    AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel);
    // Формируем запросы
    LoginRequest req = LoginRequest.newBuilder().setLogin("user").setPassword("qwerty").build();
    // Вызываем удалённые методы
    RoleStatus res = stub.giveRole(req);
    System.out.println("Success: " + res.getSuccess());
    // Завершаем работу
    channel.shutdown();
  }
}
