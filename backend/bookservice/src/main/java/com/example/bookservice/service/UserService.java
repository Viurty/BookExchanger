package com.example.bookservice.service;

import com.example.bookservice.auth.*;
import com.example.bookservice.auth.AuthServiceGrpc.AuthServiceBlockingStub;
import com.example.bookservice.model.UserDto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
  private final AuthServiceBlockingStub stub;

  @Autowired
  public UserService(int port) {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
    this.stub = AuthServiceGrpc.newBlockingStub(channel);
  }

  public boolean registerUser(UserDto user) {
    RegisterData req =
        RegisterData.newBuilder()
            .setLogin(user.getLogin())
            .setPassword(user.getPassword())
            .setPhone(user.getPhone())
            .setRole(user.getRole())
            .build();

    try {
      RegisterStatus res = stub.registerUser(req);
      if (!res.getSuccess()) {
        System.err.println("Ошибка при регистрации: " + res.getMsgError());
      }
      return res.getSuccess();
    } catch (Exception e) {
      System.err.println("Ошибка со стороны сервера: " + e.getMessage());
      return false;
    }
  }

  private String AuthUser(UserDto user) {
    LoginRequest req =
        LoginRequest.newBuilder().setLogin(user.getLogin()).setPassword(user.getPassword()).build();

    try {
      SessionToken res = stub.authUser(req);
      if (!res.getSuccess()) {
        System.err.println("Ошибка при авторизации: " + res.getMsgError());
        return "";
      }
      return res.getToken();
    } catch (Exception e) {
      System.err.println("Ошибка со стороны сервера: " + e.getMessage());
      return "";
    }
  }

  private UserDto GetUserData(String token) {
    SessionToken req = SessionToken.newBuilder().setToken(token).build();

    try {
      UserData res = stub.getUserData(req);

      if (res.getIsActive()) {
        System.err.println("Неверный токен или пользователь не найден.");
        return null;
      }

      return new UserDto(res.getLogin(), "", res.getRole(), res.getPhone(), res.getIsActive());
    } catch (Exception e) {
      System.err.println("Ошибка со стороны сервера: " + e.getMessage());
      return null;
    }
  }

  private boolean GiveRole(UserDto user) {
    UpdateRequest req =
        UpdateRequest.newBuilder().setLogin(user.getLogin()).setRole(user.getRole()).build();

    try {
      UpdateStatus res = stub.giveRole(req);
      if (!res.getSuccess()) {
        System.err.println("Ошибка при обновлении роли: " + res.getMsgError());
      }
      return res.getSuccess();
    } catch (Exception e) {
      System.err.println("Ошибка со стороны сервера: " + e.getMessage());
      return false;
    }
  }
}
