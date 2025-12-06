package com.liu.qrRoutes.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Routes implements Serializable {
  private Integer id;
  private String title;
  private String description;
  private String qrUrl;
  private String createTime;
  private Byte isDeleted;
  private Integer userId;
}
