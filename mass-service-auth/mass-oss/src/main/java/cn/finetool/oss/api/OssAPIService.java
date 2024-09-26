package cn.finetool.oss.api;

import cn.finetool.oss.controller.ImageFileStoreController;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/oss/api")
@Slf4j
public class OssAPIService {

    @Resource
    private ImageFileStoreController imageFileStoreController;

    @PostMapping(value = "uploadFile")
    public String uploadFileToMinio(@RequestBody byte[] fileBytes,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("contentType") String contentType){

        return imageFileStoreController.uploadImage(fileBytes, fileName, contentType);
    }
}
