package com.example.bookservice.controller;

import com.example.bookservice.model.LoginRequestDto;
import com.example.bookservice.model.TokenDto;
import com.example.bookservice.model.UserDto;
import com.example.bookservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "${frontend.address:http://localhost:5173}")
@Tag(name = "Users", description = "API для управления пользователями и аутентификацией")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Зарегистрировать нового пользователя")
  @PostMapping
  public ResponseEntity<Map<String, String>> createUser(@RequestBody LoginRequestDto user) {
    userService.registerUser(user);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of("message", "Пользователь зарегистрирован!"));
  }

  @Operation(summary = "Получить JWT токен по логину и паролю")
  @PostMapping("/token")
  public TokenDto getTokenUser(@RequestBody LoginRequestDto user) {
    return userService.authUser(user);
  }

  @Operation(summary = "Проверить и вернуть данные пользователя по токену")
  @PostMapping("/valid")
  public UserDto getUsersByUser(@RequestBody TokenDto token) {
    return userService.getUser(token);
  }

  @Operation(summary = "Выдать пользователю новую роль")
  @PostMapping("/role/{code}")
  public ResponseEntity<Void> giveRoleToUser(
      @RequestBody LoginRequestDto user, @PathVariable String code) {
    userService.giveRole(user, code);
    return ResponseEntity.noContent().build();
  }
}
