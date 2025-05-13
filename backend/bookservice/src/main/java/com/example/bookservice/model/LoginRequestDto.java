package com.example.bookservice.model;

import com.example.bookservice.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
  private String login;
  private String role;
  private String phone;
  private String password;
}
