package com.liu.qrRoutes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadStaticResponse {
  /**
   * 可直接访问的相对路径，例如 /static/avatar/xxx.png
   */
  private String path;

  /**
   * 保存到磁盘的文件名
   */
  private String filename;
}
