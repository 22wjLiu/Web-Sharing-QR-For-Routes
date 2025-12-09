package com.liu.qrRoutes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页数据结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
  private long total;
  private List<T> records;
}
