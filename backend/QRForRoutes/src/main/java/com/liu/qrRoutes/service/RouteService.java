package com.liu.qrRoutes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.qrRoutes.model.PageResult;
import com.liu.qrRoutes.model.Route;
import com.liu.qrRoutes.model.RouteOverview;

import java.util.List;

public interface RouteService extends IService<Route> {

  PageResult<Route> getUserListWithPageAndQuery(Integer page, Integer pageSize, String keyword, Integer userId, String startTime, String endTime);

  boolean deleteRouteByIds(List<Integer> ids);

  boolean insert(Route route);

  PageResult<Route> getRoutesByUser(Integer page, Integer pageSize, Integer userId);

  PageResult<Route> getFavoriteRoutes(Integer page, Integer pageSize, Integer userId);

  PageResult<RouteOverview> getRouteOverviewPage(Integer page, Integer pageSize, String keyword, Integer currentUserId);
}
