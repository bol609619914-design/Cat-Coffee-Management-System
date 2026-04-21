package com.catcoffee.backend.storage;

import com.catcoffee.backend.config.StorageProperties;
import com.catcoffee.backend.exception.BusinessException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class LocalFileStorageService implements FileStorageService {

    private final StorageProperties storageProperties;

    public LocalFileStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public StoredFile store(MultipartFile file, String biz) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String safeBiz = StringUtils.hasText(biz) ? biz : "common";
        String dateFolder = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path relativePath = Paths.get(safeBiz, dateFolder, fileName);
        Path uploadRoot = Paths.get(storageProperties.getUploadDir()).toAbsolutePath().normalize();
        Path targetPath = uploadRoot.resolve(relativePath).normalize();

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath);
        } catch (IOException ex) {
            throw new BusinessException("文件上传失败");
        }

        String normalizedRelativePath = relativePath.toString().replace("\\", "/");
        String baseUrl = storageProperties.getPublicBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return new StoredFile(fileName, baseUrl + "/uploads/" + normalizedRelativePath, file.getSize());
    }
}
