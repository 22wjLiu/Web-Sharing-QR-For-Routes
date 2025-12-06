package com.liu.qrRoutes.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用返回结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response<T> {

  /**
   * 是否成功
   */
  private boolean success;

  /**
   * 业务状态码，0 一般表示成功，非 0 表示各种业务错误
   */
  private int code;

  /**
   * 提示信息
   */
  private String message;

  /**
   * 具体数据
   */
  private T data;

  /* ================= 静态构造方法 ================= */

  public static <T> Response<T> ok() {
    return Response.<T>builder()
      .success(true)
      .code(200)
      .message("OK")
      .build();
  }

  public static <T> Response<T> ok(T data) {
    return Response.<T>builder()
      .success(true)
      .code(200)
      .message("OK")
      .data(data)
      .build();
  }

  public static <T> Response<T> ok(String message, T data) {
    return Response.<T>builder()
      .success(true)
      .code(200)
      .message(message)
      .data(data)
      .build();
  }

  public static <T> Response<T> failed(String message) {
    return Response.<T>builder()
      .success(false)
      .code(500)
      .message(message)
      .build();
  }

  public static <T> Response<T> failed(int code, String message) {
    return Response.<T>builder()
      .success(false)
      .code(code)
      .message(message)
      .build();
  }
}
