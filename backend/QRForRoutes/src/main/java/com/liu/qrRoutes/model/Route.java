package com.liu.qrRoutes.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("route")
public class Route implements Serializable {
  private Integer id;
  private String title;
  private String description;
  private String qrUrl;
  private String coverUrl;
  private String createTime;
  private Byte isDeleted;
  private Integer userId;
  private Byte status;

  @TableField(exist = false)
  private Long favoritesCount;

  @TableField(exist = false)
  private Boolean favorited;
}
