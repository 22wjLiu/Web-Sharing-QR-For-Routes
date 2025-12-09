package com.liu.qrRoutes.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddUserRequest {
  @NotBlank
  private String name;
  @Email(message = "邮箱格式不正确")
  private String email;

  private String avatarUrl;
  @NotNull
  private Byte role;
  @NotBlank
  private String password;
  @NotBlank
  private String description;
}
