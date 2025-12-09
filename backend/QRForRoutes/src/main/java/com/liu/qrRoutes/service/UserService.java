package com.liu.qrRoutes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.qrRoutes.model.PageResult;
import com.liu.qrRoutes.model.User;
import com.liu.qrRoutes.model.UserProfile;

import java.util.List;

public interface UserService extends IService<User> {
  User getOneByEmail(String email);

  User getOneByName(String name);

  PageResult<UserProfile> getUserListWithPageAndKeyword(Integer page, Integer pageSize, String keyword);

  boolean existsByEmail(String email);

  boolean existsByName(String name);

  long existCountByIds(List<Integer> ids);

  boolean insert(User user);

  boolean deleteUserByIds(List<Integer> ids);
}
