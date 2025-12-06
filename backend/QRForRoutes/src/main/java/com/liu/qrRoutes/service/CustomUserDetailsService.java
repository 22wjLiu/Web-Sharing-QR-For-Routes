package com.liu.qrRoutes.service;

import com.liu.qrRoutes.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 支持使用邮箱或用户名登录
    User user = userService.getOneByEmail(username);
    if (user == null) {
      user = userService.getOneByName(username);
    }
    if (user == null) {
      throw new UsernameNotFoundException("用户不存在");
    }

    return org.springframework.security.core.userdetails.User
      .withUsername(user.getEmail())
      .password(user.getPassword())
      .authorities("ROLE_USER")
      .build();
  }
}
