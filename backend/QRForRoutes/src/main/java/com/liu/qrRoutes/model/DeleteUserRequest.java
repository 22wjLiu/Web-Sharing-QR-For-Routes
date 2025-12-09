package com.liu.qrRoutes.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteUserRequest {
  @NotEmpty(message = "请选择需要删除的用户")
  private List<Integer> ids;
}
