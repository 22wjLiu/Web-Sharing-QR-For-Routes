package com.liu.qrRoutes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.qrRoutes.mapper.FavoriteMapper;
import com.liu.qrRoutes.mapper.RouteMapper;
import com.liu.qrRoutes.model.PageResult;
import com.liu.qrRoutes.model.Route;
import com.liu.qrRoutes.model.RouteOverview;
import com.liu.qrRoutes.model.User;
import com.liu.qrRoutes.model.Favorite;
import com.liu.qrRoutes.service.RouteService;
import com.liu.qrRoutes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route> implements RouteService {

  private final RouteMapper routeMapper;
  private final FavoriteMapper favoriteMapper;
  private final UserService userService;

  @Override
  public PageResult<Route> getUserListWithPageAndQuery(Integer page, Integer pageSize, String keyword, Integer userId, String startTime, String endTime) {
    var query = Wrappers.<Route>lambdaQuery()
      .ne(Route::getIsDeleted, (byte) 1);

    if (StringUtils.hasText(keyword)) {
      String trimmed = keyword.trim();
      query.like(Route::getTitle, trimmed);
    }

    if (userId != null) {
      query.eq(Route::getUserId, userId);
    }

    if (StringUtils.hasText(startTime)) {
      query.ge(Route::getCreateTime, startTime.trim());
    }
    if (StringUtils.hasText(endTime)) {
      query.le(Route::getCreateTime, endTime.trim());
    }

    Page<Route> pageResult = this.page(new Page<>(page, pageSize), query);

    return new PageResult<>(pageResult.getTotal(), pageResult.getRecords());
  }

  @Override
  public boolean deleteRouteByIds(List<Integer> ids) {
    return this.update(
      Wrappers.<Route>lambdaUpdate()
        .in(Route::getId, ids)
        .set(Route::getIsDeleted, (byte) 1)
    );
  }

  @Override
  public boolean insert(Route route) {
    return routeMapper.insert(route) > 0;
  }

  @Override
  public PageResult<Route> getRoutesByUser(Integer page, Integer pageSize, Integer userId) {
    var query = Wrappers.<Route>lambdaQuery()
      .eq(Route::getUserId, userId)
      .ne(Route::getIsDeleted, (byte) 1);

    Page<Route> pageResult = this.page(new Page<>(page, pageSize), query);
    return new PageResult<>(pageResult.getTotal(), pageResult.getRecords());
  }

  @Override
  public PageResult<Route> getFavoriteRoutes(Integer page, Integer pageSize, Integer userId) {
    List<Integer> routeIds = favoriteMapper.selectList(
      Wrappers.<Favorite>lambdaQuery().eq(Favorite::getUserId, userId)
    ).stream().map(Favorite::getRouteId).distinct().toList();

    if (routeIds.isEmpty()) {
      return new PageResult<>(0, List.of());
    }

    Page<Route> pageResult = this.page(
      new Page<>(page, pageSize),
      Wrappers.<Route>lambdaQuery()
        .in(Route::getId, routeIds)
        .ne(Route::getIsDeleted, (byte) 1)
    );

    List<Route> routes = pageResult.getRecords();
    Map<Integer, Long> favoriteCountMap = favoriteMapper.selectList(
      Wrappers.<Favorite>lambdaQuery().in(Favorite::getRouteId, routeIds)
    ).stream().collect(Collectors.groupingBy(Favorite::getRouteId, Collectors.counting()));

    routes.forEach(route -> {
      Integer id = route.getId();
      route.setFavoritesCount(favoriteCountMap.getOrDefault(id, 0L));
      route.setFavorited(true);
    });

    return new PageResult<>(pageResult.getTotal(), routes);
  }

  @Override
  public PageResult<RouteOverview> getRouteOverviewPage(Integer page, Integer pageSize, String keyword, Integer currentUserId) {
    var query = Wrappers.<Route>lambdaQuery()
      .ne(Route::getIsDeleted, (byte) 1)
      .eq(Route::getStatus, (byte) 1);

    if (StringUtils.hasText(keyword)) {
      String trimmed = keyword.trim();
      query.like(Route::getTitle, trimmed);
    }

    Page<Route> pageResult = this.page(new Page<>(page, pageSize), query);
    List<Route> routes = pageResult.getRecords();
    if (routes.isEmpty()) {
      return new PageResult<>(0, List.of());
    }

    Set<Integer> routeIds = routes.stream()
      .map(Route::getId)
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());

    Map<Integer, Long> favoriteCountMap = favoriteMapper.selectList(
      Wrappers.<Favorite>lambdaQuery().in(Favorite::getRouteId, routeIds)
    ).stream().collect(Collectors.groupingBy(Favorite::getRouteId, Collectors.counting()));

    Map<Integer, Boolean> favoritedMap;
    if (currentUserId != null) {
      favoritedMap = favoriteMapper.selectList(
        Wrappers.<Favorite>lambdaQuery()
          .eq(Favorite::getUserId, currentUserId)
          .in(Favorite::getRouteId, routeIds)
      ).stream().collect(Collectors.toMap(Favorite::getRouteId, f -> true, (a, b) -> a));
    } else {
      favoritedMap = Map.of();
    }

    Set<Integer> userIds = routes.stream()
      .map(Route::getUserId)
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());
    Map<Integer, User> userMap = userService.listByIds(userIds).stream()
      .collect(Collectors.toMap(User::getId, Function.identity()));

    List<RouteOverview> overviewList = routes.stream().map(route -> {
      Integer routeId = route.getId();
      RouteOverview vo = new RouteOverview();
      vo.setId(routeId);
      vo.setTitle(route.getTitle());
      vo.setDescription(route.getDescription());
      vo.setQrUrl(route.getQrUrl());
      vo.setCoverUrl(route.getCoverUrl());
      vo.setCreateTime(route.getCreateTime());
      vo.setStatus(route.getStatus());
      vo.setUserId(route.getUserId());

      User author = route.getUserId() != null ? userMap.get(route.getUserId()) : null;
      vo.setAuthorName(author != null ? author.getName() : "");
      vo.setAuthorAvatar(author != null ? author.getAvatarUrl() : "");

      vo.setFavoritesCount(favoriteCountMap.getOrDefault(routeId, 0L));
      vo.setFavorited(favoritedMap.getOrDefault(routeId, false));
      return vo;
    }).toList();

    return new PageResult<>(pageResult.getTotal(), overviewList);
  }
}
