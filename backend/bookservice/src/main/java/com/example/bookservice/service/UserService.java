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
    String login = user.getLogin();
    String password = user.getPassword();
    String phone = user.getPhone();
    Role role = user.getRole();
    if (phone.isEmpty() || password.isEmpty() || login.isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных для регистрации");
    }
    RegisterData req =
        RegisterData.newBuilder()
            .setLogin(login)
            .setPassword(password)
            .setPhone(phone)
            .setRole(role)
            .build();
    try {
      RegisterStatus res = stub.registerUser(req);
    } catch (Exception e) {
      throw new HttpStatusException(
          HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера: " + e.getMessage());
    }
  }

  public String authUser(LoginRequestDto user) {
    LoginRequest req =
        LoginRequest.newBuilder().setLogin(user.getLogin()).setPassword(user.getPassword()).build();

    try {
      SessionToken res = stub.authUser(req);
      if (!res.getSuccess()) {
        throw new HttpStatusException(
            HttpStatus.BAD_GATEWAY, "Ошибка при авторизации: " + res.getMsgError());
      }
      return res.getToken();
    } catch (Exception e) {
      throw new HttpStatusException(
          HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера: " + e.getMessage());
    }
  }

  public UserDto getUser(String token) {
    SessionToken req = SessionToken.newBuilder().setToken(token).build();

    try {
      UserData res = stub.getUserData(req);
      if (res.getIsActive()) {
        System.err.println("Неверный токен или пользователь не найден.");
        throw new HttpStatusException(
            HttpStatus.NOT_ACCEPTABLE, "Неверный токен или пользователь не найден.");
      }

      return new UserDto(res.getLogin(), res.getRole(), res.getPhone(), res.getIsActive());
    } catch (Exception e) {
      throw new HttpStatusException(
          HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера: " + e.getMessage());
    }
  }

  public void giveRole(UserDto user, String password) {
    if (!(password == secretCode)) {
      throw new HttpStatusException(HttpStatus.FORBIDDEN, "Нет доступа");
    }

    UpdateRequest req =
        UpdateRequest.newBuilder().setLogin(user.getLogin()).setRole(user.getRole()).build();

    try {
      UpdateStatus res = stub.giveRole(req);
      if (!res.getSuccess()) {
        throw new HttpStatusException(
            HttpStatus.BAD_GATEWAY, "Ошибка при авторизации: " + res.getMsgError());
      }
    } catch (Exception e) {
      throw new HttpStatusException(
          HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера: " + e.getMessage());
    }
  }
}
