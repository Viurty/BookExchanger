package com.example.bookservice.service;

import com.example.bookservice.auth.*;
import com.example.bookservice.auth.AuthServiceGrpc.AuthServiceBlockingStub;
import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.LoginRequestDto;
import com.example.bookservice.model.TokenDto;
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
    String roleString = user.getRole();
    Role roleEnum = Role.USER;

    if (roleString == null || roleString.isEmpty()) {
      roleString = "USER";
    }
    roleString = roleString.trim().toUpperCase();
    if (roleString.equals("ADMIN")) {
      roleEnum = Role.ADMIN;
    }

    if (phone == null
        || password == null
        || login == null
        || phone.trim().isEmpty()
        || password.trim().isEmpty()
        || login.trim().isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных.");
    }
    RegisterData req =
        RegisterData.newBuilder()
            .setLogin(login.trim())
            .setPassword(password.trim())
            .setPhone(phone.trim())
            .setRole(roleEnum)
            .build();

    RegisterStatus res;
    try {
      res = stub.registerUser(req);
    } catch (Exception e) {
      System.err.printf("ERROR: %s \n", e.getMessage());
      throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера.");
    }
    if (!res.getSuccess()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, res.getMsgError());
    }
  }

  public TokenDto authUser(LoginRequestDto user) {
    String login = user.getLogin();
    String password = user.getPassword();
    if (password == null || login == null || password.trim().isEmpty() || login.trim().isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных.");
    }
    LoginRequest req =
        LoginRequest.newBuilder().setLogin(login.trim()).setPassword(password.trim()).build();

    SessionToken res;
    try {
      res = stub.authUser(req);
    } catch (Exception e) {
      System.err.printf("ERROR: %s \n", e.getMessage());
      throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера.");
    }
    if (!res.getSuccess()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, res.getMsgError());
    }
    return new TokenDto(res.getToken());
  }

  public UserDto getUser(TokenDto tokenDto) {
    String token = tokenDto.getToken();
    if (token == null || token.trim().isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных.");
    }
    SessionToken req = SessionToken.newBuilder().setToken(token.trim()).build();
    UserData res;
    try {
      res = stub.getUserData(req);
    } catch (Exception e) {
      System.err.printf("ERROR: %s \n", e.getMessage());
      throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера.");
    }
    if (!res.getIsActive()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Невалидный токен!");
    }
    return new UserDto(res.getLogin(), res.getRole(), res.getPhone(), res.getIsActive());
  }

  public void giveRole(LoginRequestDto user, String password) {
    if (!password.trim().equals(secretCode.trim())) {
      throw new HttpStatusException(HttpStatus.FORBIDDEN, "Нет доступа.");
    }

    String login = user.getLogin();
    String roleString = user.getRole();
    Role roleEnum = Role.USER;

    if (roleString == null
        || login == null
        || roleString.trim().isEmpty()
        || login.trim().isEmpty()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Недостаточно данных.");
    }
    roleString = roleString.trim().toUpperCase();
    if (roleString.equals("ADMIN")) {
      roleEnum = Role.ADMIN;
    }
    UpdateRequest req = UpdateRequest.newBuilder().setLogin(login.trim()).setRole(roleEnum).build();

    UpdateStatus res;
    try {
      res = stub.giveRole(req);
    } catch (Exception e) {
      System.err.printf("ERROR: %s \n", e.getMessage());
      throw new HttpStatusException(HttpStatus.BAD_GATEWAY, "Ошибка со стороны сервера.");
    }
    if (!res.getSuccess()) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, res.getMsgError());
    }
  }
}
