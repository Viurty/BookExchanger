package com.example.bookservice.service;

import com.example.bookservice.auth.*;
import com.example.bookservice.auth.AuthServiceGrpc.AuthServiceBlockingStub;
import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.LoginRequestDto;
import com.example.bookservice.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final String secretCode;
  private final AuthServiceBlockingStub stub;

  @Autowired
  public UserService(
      AuthServiceGrpc.AuthServiceBlockingStub stub,
      @Value("${grpc.auth-service-secret}") String secretCode) {
    this.stub = stub;
    this.secretCode = secretCode;
  }

  public void registerUser(LoginRequestDto user) {
    String login = user.getLogin().trim();
    String password = user.getPassword().trim();
    String phone = user.getPhone().trim();
    String roleString = user.getRole();
    if (!roleString.isEmpty()) {
      roleString = roleString.trim().toUpperCase();
    }
    Role roleEnum = Role.USER;
    if (roleString.equals("ADMIN")) {
      roleEnum = Role.ADMIN;
    }
    if (phone.isEmpty() || password.isEmpty() || login.isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных для регистрации");
    }
    RegisterData req =
        RegisterData.newBuilder()
            .setLogin(login)
            .setPassword(password)
            .setPhone(phone)
            .setRole(roleEnum)
            .build();

    RegisterStatus res;
    try {
      res = stub.registerUser(req);
    } catch (Exception e) {
      throw new HttpStatusException(
          HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера: " + e.getMessage());
    }
    if (!res.getSuccess()) {
      throw new HttpStatusException(
          HttpStatus.BAD_REQUEST, "Ошибка при авторизации: " + res.getMsgError());
    }
  }

  public String authUser(LoginRequestDto user) {
    String login = user.getLogin().trim();
    String password = user.getPassword().trim();
    String roleString = user.getRole();
    if (password.isEmpty() || login.isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных для авторизации");
    }
    LoginRequest req = LoginRequest.newBuilder().setLogin(login).setPassword(password).build();

    SessionToken res;
    try {
      res = stub.authUser(req);
    } catch (Exception e) {
      throw new HttpStatusException(
          HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера: " + e.getMessage());
    }
    if (!res.getSuccess()) {
      throw new HttpStatusException(
          HttpStatus.BAD_REQUEST, "Ошибка при авторизации: " + res.getMsgError());
    }
    return res.getToken();
  }

  public UserDto getUser(String token) {
    SessionToken req = SessionToken.newBuilder().setToken(token).build();
    UserData res;
    try {
      res = stub.getUserData(req);
    } catch (Exception e) {
      throw new HttpStatusException(
          HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера: " + e.getMessage());
    }
    if (!res.getIsActive()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Невалидный токен!");
    }
    return new UserDto(res.getLogin(), res.getRole(), res.getPhone(), res.getIsActive());
  }

  public void giveRole(UserDto user, String password) {
    if (!password.trim().equals(secretCode.trim())) {
      System.err.printf("password: %s \n secret_password: %s\n", password, secretCode);
      throw new HttpStatusException(HttpStatus.FORBIDDEN, "Нет доступа");
    }

    UpdateRequest req =
        UpdateRequest.newBuilder().setLogin(user.getLogin()).setRole(user.getRole()).build();

    UpdateStatus res;
    try {
      res = stub.giveRole(req);
    } catch (Exception e) {
      throw new HttpStatusException(
          HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера: " + e.getMessage());
    }
    if (!res.getSuccess()) {
      throw new HttpStatusException(
          HttpStatus.BAD_REQUEST, "Ошибка при выдаче роли: " + res.getMsgError());
    }
  }
}
