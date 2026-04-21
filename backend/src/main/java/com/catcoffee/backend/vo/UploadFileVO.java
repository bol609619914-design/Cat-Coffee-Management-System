package com.catcoffee.backend.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "上传文件结果")
public class UploadFileVO {

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "访问地址")
    private String url;

    @Schema(description = "文件大小，单位字节")
    private Long size;
}
