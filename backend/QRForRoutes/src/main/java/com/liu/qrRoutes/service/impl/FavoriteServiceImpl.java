package com.liu.qrRoutes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.qrRoutes.mapper.FavoriteMapper;
import com.liu.qrRoutes.model.Favorite;
import com.liu.qrRoutes.service.FavoriteService;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
  @Override
  public boolean existsByUserIdAndRouteId(Integer userId, Integer routeId) {
    return this.count(
      Wrappers.<Favorite>lambdaQuery()
        .eq(Favorite::getUserId, userId)
        .eq(Favorite::getRouteId, routeId)
    ) > 0;
  }

  @Override
  public boolean deleteByUserAndRouteId(Integer userId, Integer routeId) {
    return this.remove(
      Wrappers.<Favorite>lambdaQuery()
        .eq(Favorite::getUserId, userId)
        .eq(Favorite::getRouteId, routeId)
    );
  }
}
