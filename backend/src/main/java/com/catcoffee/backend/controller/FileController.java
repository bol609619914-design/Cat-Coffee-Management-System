package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.storage.FileStorageService;
import com.catcoffee.backend.storage.StoredFile;
import com.catcoffee.backend.vo.UploadFileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "文件上传")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "上传图片或文件")
    public ApiResponse<UploadFileVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "common") @Parameter(description = "业务分类，如 cat 或 drink") String biz) {
        StoredFile storedFile = fileStorageService.store(file, biz);
        return ApiResponse.success("上传成功", new UploadFileVO(
                storedFile.getFileName(),
                storedFile.getUrl(),
                storedFile.getSize()
        ));
    }
}
