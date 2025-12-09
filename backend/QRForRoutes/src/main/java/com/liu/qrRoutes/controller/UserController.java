package com.liu.qrRoutes.controller;

import com.liu.qrRoutes.config.Constants;
import com.liu.qrRoutes.config.ResourceConfig;
import com.liu.qrRoutes.config.Response;
import com.liu.qrRoutes.model.*;
import com.liu.qrRoutes.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final ResourceConfig resourceConfig;

  @GetMapping("/list")
  public Response<PageResult<UserProfile>> listUsers(
    @RequestParam(defaultValue = "1") Integer page,
    @RequestParam(defaultValue = "10") Integer pageSize,
    @RequestParam(required = false) String keyword
  ) {
    page = Math.max(page, 1);
    pageSize = Math.max(pageSize, 1);
    return Response.ok(userService.getUserListWithPageAndKeyword(page, pageSize, keyword));
  }

  @PostMapping("/add")
  public Response<Void> addUser(@Valid @RequestBody AddUserRequest request) {
    User user = new User();

    if (StringUtils.hasText(request.getEmail())) {
      User existUser = userService.getOneByEmail(request.getEmail());
      if (existUser != null && !Objects.equals(existUser.getId(), user.getId())) {
        return Response.failed(Constants.ResponseCode.DATA_ERROR, "邮箱已被占用");
      }
    }

    if (StringUtils.hasText(request.getName())) {
      User existUser = userService.getOneByName(request.getName());
      if (existUser != null && !Objects.equals(existUser.getId(), user.getId())) {
        return Response.failed(Constants.ResponseCode.DATA_ERROR, "用户名已被占用");
      }
    }

    user.setEmail(request.getEmail());
    user.setName(request.getName());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());
    if(StringUtils.hasText(request.getDescription())) {
      user.setDescription(request.getDescription());
    }
    if(StringUtils.hasText(request.getAvatarUrl())) {
      user.setAvatarUrl(request.getAvatarUrl());
    } else {
      user.setAvatarUrl("/static/" + resourceConfig.getAvatarDir() + "/" + resourceConfig.getAvatarDefault());
    }

    if(userService.insert(user)) {
      return Response.ok("添加成功");
    }

    return Response.failed("添加失败");
  }

  @PutMapping("/password")
  public Response<Void> updatePassword(Authentication authentication, @Valid @RequestBody UpdatePassWordRequest request) {
    if (authentication == null) {
      return Response.failed(Constants.ResponseCode.NO_PERMISSION, "未登录");
    }

    if (!StringUtils.hasText(request.getOldPassWord()) || !StringUtils.hasText(request.getNewPassWord())) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "密码不能为空");
    }

    org.springframework.security.core.userdetails.User principal =
      (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
    User user = userService.getOneByEmail(principal.getUsername());
    if (user == null || Objects.equals(user.getIsDeleted(), (byte) 1)) {
      return Response.failed(Constants.ResponseCode.NOTFOUND, "用户不存在");
    }

    if (!passwordEncoder.matches(request.getOldPassWord(), user.getPassword())) {
      return Response.failed(Constants.ResponseCode.NO_PERMISSION, "原密码错误");
    }
    if (passwordEncoder.matches(request.getNewPassWord(), user.getPassword())) {
      return Response.failed(Constants.ResponseCode.DATA_ERROR, "新密码不能与旧密码相同");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassWord()));
    boolean updated = userService.updateById(user);
    if (!updated) {
      return Response.failed("密码更新失败");
    }
    return Response.ok("密码更新成功");
  }

  @PutMapping("/{id}")
  public Response<Void> updateUser(@PathVariable Integer id, @Valid @RequestBody UpdateUserRequest request) {
    if (!Objects.equals(id, request.getId())) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "路径参数与请求体 ID 不一致");
    }

    User user = userService.getById(id);
    if (user == null || Objects.equals(user.getIsDeleted(), (byte) 1)) {
      return Response.failed(Constants.ResponseCode.NOTFOUND, "用户不存在");
    }

    if (StringUtils.hasText(request.getEmail())) {
      User existUser = userService.getOneByEmail(request.getEmail());
      if (existUser != null && !Objects.equals(existUser.getId(), user.getId())) {
        return Response.failed(Constants.ResponseCode.DATA_ERROR, "邮箱已被占用");
      }
    }

    if (StringUtils.hasText(request.getName())) {
      User existUser = userService.getOneByName(request.getName());
      if (existUser != null && !Objects.equals(existUser.getId(), user.getId())) {
        return Response.failed(Constants.ResponseCode.DATA_ERROR, "用户名已被占用");
      }
    }

    if (request.getName() != null) {
      user.setName(request.getName());
    }
    if (request.getEmail() != null) {
      user.setEmail(request.getEmail());
    }
    if (request.getAvatarUrl() != null) {
      user.setAvatarUrl(request.getAvatarUrl());
    }
    if (request.getRole() != null) {
      user.setRole(request.getRole());
    }
    if (request.getDescription() != null) {
      user.setDescription(request.getDescription());
    }

    boolean updated = userService.updateById(user);
    if (!updated) {
      return Response.failed("更新失败");
    }
    return Response.ok("更新成功");
  }

  @DeleteMapping
  public Response<Void> deleteUsers(@Valid @RequestBody DeleteUserRequest request) {
    List<Integer> ids = request.getIds().stream()
      .filter(Objects::nonNull)
      .distinct()
      .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(ids)) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "未提供有效的用户");
    }

    if (userService.existCountByIds(ids) == 0) {
      return Response.failed(Constants.ResponseCode.NOTFOUND, "用户不存在或已被删除");
    }

    if (!userService.deleteUserByIds(ids)) {
      return Response.failed("删除失败");
    }
    return Response.ok("删除成功");
  }
}
