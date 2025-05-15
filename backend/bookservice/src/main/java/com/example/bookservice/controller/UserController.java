package com.example.bookservice.controller;

import com.example.bookservice.model.LoginRequestDto;
import com.example.bookservice.model.TokenDto;
import com.example.bookservice.model.UserDto;
import com.example.bookservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping()
  public ResponseEntity<String> createUser(@RequestBody LoginRequestDto user) {
    userService.registerUser(user);
    return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь зарегестрирован!");
  }

  @PostMapping("/token")
  public TokenDto getTokenUser(@RequestBody LoginRequestDto user) {
    return userService.authUser(user);
  }

  @PostMapping("/valid")
  public UserDto getUsersByUser(@RequestBody TokenDto token) {
    return userService.getUser(token);
  }

  @PostMapping("/role/{code}")
  public ResponseEntity<String> getOwnersByUser(
      @RequestBody LoginRequestDto user, @PathVariable String code) {
    userService.giveRole(user, code);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .body("Пользователь теперь имеет новую роль!");
  }
}
