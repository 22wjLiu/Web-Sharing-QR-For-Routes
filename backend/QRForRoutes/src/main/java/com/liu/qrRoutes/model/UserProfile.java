package com.liu.qrRoutes.model;

import lombok.Data;

@Data
public class UserProfile {
  private Integer id;
  private String name;
  private String email;
  private String avatarUrl;
  private String description;
  private Byte role;

  public static UserProfile from(User user) {
    if (user == null) {
      return null;
    }
    UserProfile profile = new UserProfile();
    profile.setId(user.getId());
    profile.setName(user.getName());
    profile.setEmail(user.getEmail());
    profile.setAvatarUrl(user.getAvatarUrl());
    profile.setRole(user.getRole());
    profile.setDescription(user.getDescription());
    return profile;
  }
}
