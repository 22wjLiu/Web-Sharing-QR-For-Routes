package com.liu.qrRoutes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.qrRoutes.model.Favorite;

public interface FavoriteService extends IService<Favorite> {

  boolean existsByUserIdAndRouteId(Integer userId, Integer routeId);

  boolean deleteByUserAndRouteId(Integer userId, Integer routeId);
}
