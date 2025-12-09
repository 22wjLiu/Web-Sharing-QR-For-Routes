package com.liu.qrRoutes.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRouteRequest {
  @NotNull(message = "ID不能为空")
  private Integer id;

  private String title;
  private String description;
  private String qrUrl;
  private String coverUrl;
  private Byte status;
}
