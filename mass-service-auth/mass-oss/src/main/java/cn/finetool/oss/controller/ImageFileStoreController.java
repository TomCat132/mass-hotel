package cn.finetool.oss.controller;

import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.oss.util.FileConvertUtil;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


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
    @PostMapping("/convert")
    private String urlToBase64(@RequestParam("url") String url){
        try {
            return fileConvertUtil.convertFile(url);
        }catch (Exception e) {
            throw new BusinessRuntimeException(BusinessErrors.IMAGE_CONVERT_ERROR, "图片转换失败");
        }
    }
}
