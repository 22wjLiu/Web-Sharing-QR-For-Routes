package com.liu.qrRoutes.controller;

import com.liu.qrRoutes.config.Constants;
import com.liu.qrRoutes.config.ResourceConfig;
import com.liu.qrRoutes.config.Response;
import com.liu.qrRoutes.model.*;
import com.liu.qrRoutes.service.RouteService;
import com.liu.qrRoutes.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
public class RouteController {

  private final RouteService routeService;
  private final ResourceConfig resourceConfig;
  private final UserService userService;

  @GetMapping("/list")
  public Response<PageResult<Route>> listRoutes(
    @RequestParam(defaultValue = "1") Integer page,
    @RequestParam(defaultValue = "10") Integer pageSize,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) Integer userId,
    @RequestParam(required = false) String startTime,
    @RequestParam(required = false) String endTime
  ) {
    page = Math.max(page, 1);
    pageSize = Math.max(pageSize, 1);

    return Response.ok(routeService.getUserListWithPageAndQuery(page, pageSize, keyword, userId, startTime, endTime));
  }

  @GetMapping("/explore")
  public Response<PageResult<RouteOverview>> exploreRoutes(
    @RequestParam(defaultValue = "1") Integer page,
    @RequestParam(defaultValue = "10") Integer pageSize,
    @RequestParam(required = false) String keyword,
    Authentication authentication
  ) {
    page = Math.max(page, 1);
    pageSize = Math.max(pageSize, 1);
    Integer currentUserId = null;
    if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User principal) {
      User user = userService.getOneByEmail(principal.getUsername());
      currentUserId = user != null ? user.getId() : null;
    }
    return Response.ok(routeService.getRouteOverviewPage(page, pageSize, keyword, currentUserId));
  }

  @PostMapping("/add")
  public Response<Void> addRoute(@Valid @RequestBody AddRouteRequest request) {
    Route route = new Route();

    route.setTitle(request.getTitle());
    route.setUserId(request.getUserId());
    route.setQrUrl(request.getQrUrl());
    route.setDescription(request.getDescription());

    if (StringUtils.hasText(request.getCoverUrl())) {
      route.setCoverUrl(request.getCoverUrl());
    } else {
      route.setCoverUrl("/static/" + resourceConfig.getRouteCoverDir() + "/" + resourceConfig.getRouteCoverDefault());
    }

    if(routeService.insert(route)) {
      return Response.ok("添加成功");
    }

    return Response.failed("添加失败");
  }

  @PutMapping("/{id}")
  public Response<Void> updateRoute(@PathVariable Integer id, @Valid @RequestBody UpdateRouteRequest request) {
    if (!Objects.equals(id, request.getId())) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "路径参数与请求体 ID 不一致");
    }

    Route route = routeService.getById(id);
    if (route == null || Objects.equals(route.getIsDeleted(), (byte) 1)) {
      return Response.failed(Constants.ResponseCode.NOTFOUND, "路线不存在");
    }

    if (request.getTitle() != null) {
      route.setTitle(request.getTitle());
    }
    if (request.getDescription() != null) {
      route.setDescription(request.getDescription());
    }
    if (request.getQrUrl() != null) {
      route.setQrUrl(request.getQrUrl());
    }
    if (request.getCoverUrl() != null) {
      route.setCoverUrl(request.getCoverUrl());
    }
    if (request.getStatus() != null) {
      route.setStatus(request.getStatus());
    }

    boolean updated = routeService.updateById(route);
    if (!updated) {
      return Response.failed("更新失败");
    }

    return Response.ok("更新成功");
  }

  @DeleteMapping
  public Response<Void> deleteRoutes(@Valid @RequestBody DeleteRouteRequest request) {
    List<Integer> ids = request.getIds().stream()
      .filter(Objects::nonNull)
      .distinct()
      .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(ids)) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "未提供有效的路线");
    }

    if (!routeService.deleteRouteByIds(ids)) {
      return Response.failed("删除失败");
    }

    return Response.ok();
  }

  @GetMapping("/user/{userId}")
  public Response<PageResult<Route>> listByUser(
    @PathVariable Integer userId,
    @RequestParam(defaultValue = "1") Integer page,
    @RequestParam(defaultValue = "10") Integer pageSize
  ) {
    if (userId == null || userId <= 0) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "用户 ID 无效");
    }
    page = Math.max(page, 1);
    pageSize = Math.max(pageSize, 1);

    return Response.ok(routeService.getRoutesByUser(page, pageSize, userId));
  }

  @GetMapping("/favorite/{userId}")
  public Response<PageResult<Route>> listFavorites(
    @PathVariable Integer userId,
    @RequestParam(defaultValue = "1") Integer page,
    @RequestParam(defaultValue = "10") Integer pageSize
  ) {
    if (userId == null || userId <= 0) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "用户 ID 无效");
    }

    page = Math.max(page, 1);
    pageSize = Math.max(pageSize, 1);

    return Response.ok(routeService.getFavoriteRoutes(page, pageSize, userId));
  }
}
