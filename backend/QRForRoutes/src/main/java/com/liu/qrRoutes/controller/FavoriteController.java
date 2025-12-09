package com.liu.qrRoutes.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liu.qrRoutes.config.Constants;
import com.liu.qrRoutes.config.Response;
import com.liu.qrRoutes.model.Favorite;
import com.liu.qrRoutes.model.Route;
import com.liu.qrRoutes.model.User;
import com.liu.qrRoutes.service.FavoriteService;
import com.liu.qrRoutes.service.RouteService;
import com.liu.qrRoutes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favor")
@RequiredArgsConstructor
public class FavoriteController {

  private final FavoriteService favoriteService;
  private final UserService userService;
  private final RouteService routeService;

  @PostMapping("/{routeId}")
  public Response<Void> addFavorite(@PathVariable Integer routeId, Authentication authentication) {
    User currentUser = resolveUser(authentication);
    if (currentUser == null) {
      return Response.failed(Constants.ResponseCode.AUTH_EXPIRED, "请先登录");
    }

    Route route = routeService.getById(routeId);
    if (route == null || route.getIsDeleted() != null && route.getIsDeleted() == 1) {
      return Response.failed(Constants.ResponseCode.NOTFOUND, "路线不存在或已删除");
    }

    if (favoriteService.existsByUserIdAndRouteId(currentUser.getId(), routeId)) {
      return Response.ok("已收藏");
    }

    Favorite favorite = new Favorite();
    favorite.setUserId(currentUser.getId());
    favorite.setRouteId(routeId);

    if (!favoriteService.save(favorite)) {
      return Response.failed("收藏失败");
    }
    return Response.ok("收藏成功");
  }

  @DeleteMapping("/{routeId}")
  public Response<Void> removeFavorite(@PathVariable Integer routeId, Authentication authentication) {
    User currentUser = resolveUser(authentication);
    if (currentUser == null) {
      return Response.failed(Constants.ResponseCode.AUTH_EXPIRED, "请先登录");
    }

    if (!favoriteService.existsByUserIdAndRouteId(currentUser.getId(), routeId)) {
      return Response.failed(Constants.ResponseCode.NOTFOUND, "收藏不存在");
    }

    if (!favoriteService.deleteByUserAndRouteId(currentUser.getId(), routeId)) {
      return Response.failed(Constants.ResponseCode.FAIL, "取消收藏失败");
    }
    return Response.ok("已取消收藏");
  }

  private User resolveUser(Authentication authentication) {
    if (authentication == null) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
      return userService.getOneByEmail(userDetails.getUsername());
    }
    return null;
  }
}
