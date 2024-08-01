package cn.finetool.api.service;

import cn.finetool.api.configuration.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@FeignClient(name = "mass-oss-service", url = "http://mass-oss-service",path = "/oss/api", configuration = FeignClientConfiguration.class)
public interface OssAPIService {

    @PostMapping(value = "upload",consumes = "application/json")
    public String uploadFileToMinio(@RequestParam("file") MultipartFile file);

    @PostMapping(value = "convert",consumes = "application/json")
    public String urlToBase64(@RequestParam("url") String url);
}
