package com.liu.qrRoutes.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("favorite")
public class Favorite implements Serializable {
  private Integer id;
  private Integer userId;
  private Integer routeId;
}
