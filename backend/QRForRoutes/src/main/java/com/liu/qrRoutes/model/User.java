package com.liu.qrRoutes.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("user")
public class User implements Serializable {
  private Integer id;
  private String name;
  private String password;
  private String email;
  private String avatarUrl;
  private String description;
  private Byte role;
  private Byte isDeleted;
}
