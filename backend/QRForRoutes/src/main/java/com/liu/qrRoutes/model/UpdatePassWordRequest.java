package com.liu.qrRoutes.model;

import lombok.Data;

@Data
public class UpdatePassWordRequest {
  private String oldPassWord;
  private String newPassWord;
}
