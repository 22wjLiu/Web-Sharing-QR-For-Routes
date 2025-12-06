package com.liu.qrRoutes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.qrRoutes.model.User;

public interface UserService  extends IService<User> {
  User getOneByEmail(String email);

  User getOneByName(String name);

  boolean existsByEmail(String email);

  boolean existsByName(String name);

  boolean insert(User user);
}
