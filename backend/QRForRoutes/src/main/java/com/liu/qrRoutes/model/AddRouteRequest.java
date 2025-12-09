package com.liu.qrRoutes.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRouteRequest {
  @NotBlank
  private String title;
  @NotBlank
  private String description;
  @NotBlank
  private String qrUrl;
  private String coverUrl;
  @NotNull
  private Integer userId;
}
