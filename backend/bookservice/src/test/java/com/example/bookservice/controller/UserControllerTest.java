package com.example.bookservice.controller;

import com.example.bookservice.exception.ExceptionController;
import com.example.bookservice.exception.HttpStatusException;
import com.example.bookservice.model.LoginRequestDto;
import com.example.bookservice.model.TokenDto;
import com.example.bookservice.model.UserDto;
import com.example.bookservice.service.UserService;
import com.example.bookservice.auth.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @InjectMocks private UserController userController;

  @Mock private UserService userService;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();

    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new ExceptionController())
            .build();
  }

  @Test
  void createUser_ReturnCreated() throws Exception {
    LoginRequestDto loginRequestDto = new LoginRequestDto("user1", "pass1", "1234567890", "USER");
    doNothing().when(userService).registerUser(any(LoginRequestDto.class));

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Пользователь зарегистрирован!"));
  }

  @Test
  void createUser_ReturnError() throws Exception {
    LoginRequestDto loginRequestDto = new LoginRequestDto("user1", "pass1", "1234567890", "USER");
    doThrow(new HttpStatusException(HttpStatus.BAD_REQUEST, "Пользователь уже существует"))
        .when(userService)
        .registerUser(any(LoginRequestDto.class));

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Пользователь уже существует"));
  }

  @Test
  void getTokenUser_ReturnToken() throws Exception {
    LoginRequestDto loginRequestDto = new LoginRequestDto("user1", "pass1", "1234567890", "USER");
    TokenDto tokenDto = new TokenDto("valid-token");
    when(userService.authUser(any(LoginRequestDto.class))).thenReturn(tokenDto);

    mockMvc
        .perform(
            post("/users/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("valid-token"));
  }

  @Test
  void getUsersByUser_ReturnUserData() throws Exception {
    TokenDto tokenDto = new TokenDto("valid-token");
    UserDto userDto = new UserDto("user1", Role.USER, "1234567890", true);
    when(userService.getUser(any(TokenDto.class))).thenReturn(userDto);

    mockMvc
        .perform(
            post("/users/valid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.login").value("user1"))
        .andExpect(jsonPath("$.role").value("USER"))
        .andExpect(jsonPath("$.phone").value("1234567890"))
        .andExpect(jsonPath("$.active").value(true));
  }

  @Test
  void getOwnersByUser_ReturnBadRequest() throws Exception {
    LoginRequestDto loginRequestDto = new LoginRequestDto("user1", "pass1", "1234567890", "USER");
    doThrow(new HttpStatusException(HttpStatus.BAD_REQUEST, "Пользователь не найден"))
        .when(userService)
        .giveRole(any(LoginRequestDto.class), anyString());

    mockMvc
        .perform(
            post("/users/role/secretcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Пользователь не найден"));
  }

  @Test
  void getOwnersByUser_ReturnNoContent() throws Exception {
    LoginRequestDto loginRequestDto = new LoginRequestDto("user1", "pass1", "1234567890", "ADMIN");
    doNothing().when(userService).giveRole(any(LoginRequestDto.class), anyString());

    mockMvc
        .perform(
            post("/users/role/secretcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
  }
}
