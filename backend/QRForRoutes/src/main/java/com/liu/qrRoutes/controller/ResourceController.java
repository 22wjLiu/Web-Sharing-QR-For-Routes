package com.liu.qrRoutes.controller;

import com.liu.qrRoutes.config.Constants;
import com.liu.qrRoutes.config.ResourceConfig;
import com.liu.qrRoutes.config.Response;
import com.liu.qrRoutes.model.UploadStaticResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {

  private static final Set<String> ALLOWED_TYPES = Set.of("avatar", "qr", "routecover");
  private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp");

  private final ResourceConfig resourceConfig;

  @PostMapping("/upload")
  public Response<UploadStaticResponse> upload(
    @RequestParam("file") MultipartFile file,
    @RequestParam("type") @NotBlank String type
  ) {
    if (file == null || file.isEmpty()) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "上传文件不能为空");
    }

    String normalizedType = type.trim().toLowerCase(Locale.ROOT);
    if (!ALLOWED_TYPES.contains(normalizedType)) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "不支持的上传类型");
    }

    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
    extension = (extension == null ? "png" : extension.trim().toLowerCase(Locale.ROOT));
    if (!IMAGE_EXTENSIONS.contains(extension)) {
      return Response.failed(Constants.ResponseCode.REQUEST_ERROR, "仅支持图片格式文件");
    }

    Path rootPath;
    try {
      rootPath = resolveRootPath(resourceConfig.getRootUrl());
    } catch (IllegalArgumentException e) {
      return Response.failed(Constants.ResponseCode.FAIL, "静态资源根目录配置错误");
    }

    String dirName = switch (normalizedType) {
      case "avatar" -> resourceConfig.getAvatarDir();
      case "routecover" -> resourceConfig.getRouteCoverDir();
      default -> resourceConfig.getQrDir();
    };
    Path targetDir = rootPath.resolve(dirName);
    try {
      Files.createDirectories(targetDir);
    } catch (IOException e) {
      return Response.failed(Constants.ResponseCode.FAIL, "创建上传目录失败");
    }

    String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;
    Path targetPath = targetDir.resolve(filename);
    try (var inputStream = file.getInputStream()) {
      Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      return Response.failed(Constants.ResponseCode.FAIL, "保存文件失败");
    }

    String relativePath = "/static/" + dirName + "/" + filename;
    return Response.ok("上传成功", new UploadStaticResponse(relativePath, filename));
  }

  private Path resolveRootPath(String rootUrl) {
    if (!StringUtils.hasText(rootUrl)) {
      throw new IllegalArgumentException("resource.root-url 未配置");
    }
    try {
      return Paths.get(URI.create(appendTrailingSlash(rootUrl)));
    } catch (IllegalArgumentException e) {
      // 如果 rootUrl 不是合法 URI，则尝试移除 file: 前缀按本地路径解析
      String normalized = rootUrl.replaceFirst("^file:", "");
      return Paths.get(normalized);
    }
  }

  private String appendTrailingSlash(String path) {
    if (path.endsWith("/")) {
      return path;
    }
    return path + "/";
  }
}
