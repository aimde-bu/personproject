package com.ssm.controller;

import com.ssm.domain.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    // 上传文件存储目录，可根据实际情况修改
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new Result(500, "上传文件不能为空", null);
        }

        // 确保上传目录存在
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名
        String newFilename = UUID.randomUUID().toString() + ext;

        try {
            // 保存文件
            File dest = new File(uploadDir, newFilename);
            file.transferTo(dest);

            // 返回可访问的URL路径
            String url = "/uploads/" + newFilename;
            return new Result(200, "上传成功", url);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(500, "上传失败: " + e.getMessage(), null);
        }
    }
}
