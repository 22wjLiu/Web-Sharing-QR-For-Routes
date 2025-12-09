package com.liu.qrRoutes.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {
  @NotNull(message = "用户ID不能为空")
  private Integer id;
  private String name;
  @Email(message = "邮箱格式不正确")
  private String email;
  private String avatarUrl;
  private Byte role;
  private String description;
}
