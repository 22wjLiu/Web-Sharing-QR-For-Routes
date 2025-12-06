package com.liu.qrRoutes.controller;

import com.liu.qrRoutes.config.Constants;
import com.liu.qrRoutes.config.ResourceConfig;
import com.liu.qrRoutes.config.Response;
import com.liu.qrRoutes.model.LoginRequest;
import com.liu.qrRoutes.model.RegisterRequest;
import com.liu.qrRoutes.model.User;
import com.liu.qrRoutes.model.UserProfile;
import com.liu.qrRoutes.service.UserService;
import com.liu.qrRoutes.service.RedisService;
import com.liu.qrRoutes.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final RedisService redisService;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final ResourceConfig resourceConfig;

  @GetMapping("/sendCode")
  public Response<Void> sendCode(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String type) {
    if(Constants.SendCode.REGISTER.equals(type)){
      if(userService.existsByName(name)) {
        return Response.failed("该用户名已注册");
      }

      if (userService.existsByEmail(email)) {
        return Response.failed("该邮箱已注册");
      }

      redisService.sendRegisterCode(email);
    }

    return Response.ok();
  }

  @PostMapping("/register")
  public Response<Void> register(@Valid @RequestBody RegisterRequest req) {
    if (!redisService.validateRegisterCode(req.getEmail(), req.getCode())) {
      return Response.failed("验证码错误或已过期");
    }

    User user = new User();
    user.setEmail(req.getEmail());
    user.setName(req.getName());
    user.setPassword(passwordEncoder.encode(req.getPassword()));
    user.setAvatarUrl("/static/" + resourceConfig.getAvatarDir() + "/" + resourceConfig.getAvatarDefault());
    if(userService.insert(user)) {
      return Response.ok();
    }

    return Response.failed("注册失败，请稍后重试");
  }

  @PostMapping("/login")
  public Response<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
    User user = resolveUserByIdentifier(req.getEmailOrName());
    if (user == null) {
      return Response.failed(400, "用户名或邮箱不存在");
    }

    UsernamePasswordAuthenticationToken authToken =
      new UsernamePasswordAuthenticationToken(user.getEmail(), req.getPassword());
    try {
      authenticationManager.authenticate(authToken);
    } catch (BadCredentialsException e) {
      return Response.failed(400, "密码错误");
    } catch (AuthenticationException e) {
      return Response.failed(401, e.getMessage());
    }

    // 生成 jti
    String jti = UUID.randomUUID().toString();

    // 生成 token
    String token = JwtUtils.generateToken(user.getId(), user.getEmail(), jti);

    // 写 Redis
    redisService.overrideLogin(user.getId(), jti);

    Map<String, String> data = new HashMap<>();
    data.put("token", token);

    return Response.ok(data);
  }

  @PostMapping("/logout")
  public Response<Void> logout(Authentication authentication) {
    if (authentication != null) {
      org.springframework.security.core.userdetails.User principal =
        (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
      User user = userService.getOneByEmail(principal.getUsername());
      redisService.removeUserInfo(user.getId());
    }
    return Response.ok();
  }

  @GetMapping("/getUserInfo")
  public Response<UserProfile> getUserInfo(Authentication authentication) {
    if (authentication == null) {
      return Response.failed(401, "未登录");
    }

    org.springframework.security.core.userdetails.User principal =
      (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
    User user = userService.getOneByEmail(principal.getUsername());
    if (user == null) {
      return Response.failed(404, "用户不存在");
    }

    return Response.ok(UserProfile.from(user));
  }

  private User resolveUserByIdentifier(String emailOrName) {
    User user = userService.getOneByEmail(emailOrName);
    if (user == null) {
      user = userService.getOneByName(emailOrName);
    }
    return user;
  }
}
