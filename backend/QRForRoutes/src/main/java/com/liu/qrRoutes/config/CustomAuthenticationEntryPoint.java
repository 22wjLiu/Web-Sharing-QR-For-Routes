package com.liu.qrRoutes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    String uri = extractOriginalUri(request);
    if (uri != null && uri.contains("/static/")) {
      Response<Void> notFound = Response.failed(Constants.ResponseCode.NOTFOUND, "资源不存在");
      writeJson(response, notFound, HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    Response<Void> body = Response.failed(Constants.ResponseCode.AUTH_EXPIRED, "登录状态已过期，请重新登录");
    writeJson(response, body, HttpServletResponse.SC_UNAUTHORIZED);
  }

  private String extractOriginalUri(HttpServletRequest request) {
    Object originalUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
    if (originalUri instanceof String uri && !uri.isEmpty()) {
      return uri;
    }
    return request.getRequestURI();
  }

  private void writeJson(HttpServletResponse response, Object data, int status) throws IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.getWriter().write(MAPPER.writeValueAsString(data));
  }
}
