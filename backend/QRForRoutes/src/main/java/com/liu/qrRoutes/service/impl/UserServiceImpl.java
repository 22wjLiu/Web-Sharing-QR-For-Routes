package com.liu.qrRoutes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.qrRoutes.mapper.UserMapper;
import com.liu.qrRoutes.model.PageResult;
import com.liu.qrRoutes.model.User;
import com.liu.qrRoutes.model.UserProfile;
import com.liu.qrRoutes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
  public PageResult<UserProfile> getUserListWithPageAndKeyword(Integer page, Integer pageSize, String keyword) {
    var query = Wrappers.<User>lambdaQuery()
      .ne(User::getIsDeleted, (byte) 1);
    if (StringUtils.hasText(keyword)) {
      String trimmed = keyword.trim();
      query.and(wrapper -> wrapper
        .like(User::getName, trimmed)
        .or()
        .like(User::getEmail, trimmed)
      );
    }

    Page<User> pageResult = this.page(new Page<>(page, pageSize), query);

    List<UserProfile> userList = pageResult.getRecords().stream()
      .map(UserProfile::from)
      .collect(Collectors.toList());

    return new PageResult<>(pageResult.getTotal(), userList);
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
  public long existCountByIds(List<Integer> ids) {
    return this.count(
      Wrappers.<User>lambdaQuery()
        .in(User::getId, ids)
        .ne(User::getIsDeleted, (byte) 1)
    );
  }

  @Override
  public boolean insert(User user) {
    return userMapper.insert(user) > 0;
  }

  @Override
  public boolean deleteUserByIds(List<Integer> ids) {
    return this.update(
      Wrappers.<User>lambdaUpdate()
        .in(User::getId, ids)
        .set(User::getIsDeleted, (byte) 1)
    );
  }
}
