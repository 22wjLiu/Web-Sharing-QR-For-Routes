package com.liu.qrRoutes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.qrRoutes.mapper.UserMapper;
import com.liu.qrRoutes.model.User;
import com.liu.qrRoutes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  private final UserMapper userMapper;

  @Override
  public User getOneByEmail(String email) {
    return this.getOne(
      Wrappers.<User>lambdaQuery()
        .eq(User::getEmail, email)
        .ne(User::getIsDeleted, (byte) 1)
        .last("LIMIT 1")
    );
  }

  @Override
  public User getOneByName(String name) {
    return this.getOne(
      Wrappers.<User>lambdaQuery()
        .eq(User::getName, name)
        .ne(User::getIsDeleted, (byte) 1)
        .last("LIMIT 1")
    );
  }

  @Override
  public boolean existsByEmail(String email) {
    return getOneByEmail(email) != null;
  }

  @Override
  public boolean existsByName(String name) {
    return getOneByName(name) != null;
  }

  @Override
  public boolean insert(User user) {
    return userMapper.insert(user) > 0;
  }
}
