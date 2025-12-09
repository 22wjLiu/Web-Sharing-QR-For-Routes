package com.liu.qrRoutes.model;

import lombok.Data;

@Data
public class RouteOverview {
  private Integer id;
  private String title;
  private String description;
  private String qrUrl;
  private String coverUrl;
  private String createTime;
  private Byte status;
  private Integer userId;

  private String authorName;
  private String authorAvatar;
  private long favoritesCount;
  private boolean favorited;
}
