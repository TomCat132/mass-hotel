package cn.finetool.oss.controller;

import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.oss.util.FileConvertUtil;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/oss/api")
public class ImageFileStoreController {

    @Resource
    FileConvertUtil fileConvertUtil;

    @Resource
    private  Environment config;

    @Autowired
    private  MinioClient minioClient;

    /** ========== 图片文件上传接口 ========== */
    @PostMapping("/upload")
    public String putMinioFile(@RequestParam("file") MultipartFile file){
        try {
            minioClient.putObject(config.getProperty("minio.bucket"),
                    file.getOriginalFilename(),
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType());
            return config.getProperty("minio.url") + '/' + config.getProperty("minio.bucket") + '/'+ file.getOriginalFilename();
        }catch (Exception e){
            return "解析失败";
        }
    }

    /** ========== 图片转换接口 ========== */
    @GetMapping("/convert")

    private String urlToBase64(@RequestParam("url") String url){
        try {
            return fileConvertUtil.convertFile(url);
        }catch (Exception e) {
            throw new BusinessRuntimeException(BusinessErrors.IMAGE_CONVERT_ERROR, "图片转换失败");
        }
    }

    public String uploadImage(byte[] fileBytes, String fileName, String contentType) {
        // 转换为 MultipartFile 对象
        MultipartFile multipartFile = new MockMultipartFile(
                "file",  // 表单字段名
                fileName,  // 文件名
                contentType,  // 内容类型
                fileBytes   // 文件内容
        );
        // 上传文件
        return putMinioFile(multipartFile);
    }
}
