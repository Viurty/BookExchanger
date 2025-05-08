package com.example.bookservice.model;

import com.example.bookservice.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
  private String login;
  private String password;
  private Role role;
  private String phone;
  private boolean isActive;
}
