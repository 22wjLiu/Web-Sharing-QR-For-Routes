package com.liu.qrRoutes.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteRouteRequest {
  @NotEmpty(message = "请选择需要删除的路线")
  private List<Integer> ids;
}
