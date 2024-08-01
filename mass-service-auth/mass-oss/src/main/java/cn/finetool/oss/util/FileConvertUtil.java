package cn.finetool.oss.util;



import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.exception.BusinessRuntimeException;
import io.minio.MinioClient;

import jakarta.annotation.Resource;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

@Component
public class FileConvertUtil {

    @Resource
    private  Environment config;

    @Autowired
    private  MinioClient minioClient;




    public String convertFile(String file) throws MalformedURLException {
        URL url = new URL(file);
        String path = url.getPath();
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        try (InputStream inputStream = minioClient.getObject(config.getProperty("mass-hotel.minio.bucket"), fileName)) {
            byte[] fileData = IOUtils.toByteArray(inputStream);
            return "data:jpg;base64," + Base64.getEncoder().encodeToString(fileData);
        } catch (Exception e) {
            throw new BusinessRuntimeException(BusinessErrors.IMAGE_CONVERT_ERROR,"图片转换失败");
        }
    }
}
