package cn.finetool.api.service;



import cn.finetool.common.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@RestController
@FeignClient(name = "mass-oss-service",path = "/oss/api",configuration = MultipartSupportConfig.class)
public interface OssAPIService {

    @PostMapping(value = "uploadFile")
    String uploadFileToMinio(@RequestBody byte[] fileBytes,
                             @RequestParam("fileName") String fileName,
                             @RequestParam("contentType") String contentType);

    @PostMapping(value = "convert")
     String urlToBase64(@RequestParam("url") String url);
}
