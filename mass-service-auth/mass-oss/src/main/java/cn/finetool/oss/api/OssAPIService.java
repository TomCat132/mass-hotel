package cn.finetool.oss.api;

import cn.finetool.common.po.FileUrl;
import cn.finetool.oss.controller.ImageFileStoreController;
import cn.finetool.oss.service.OssService;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/oss/api")
@Slf4j
public class OssAPIService {

    @Resource
    private ImageFileStoreController imageFileStoreController;
    @Resource
    private OssService ossService;

    @PostMapping(value = "uploadFile")
    public String uploadFileToMinio(@RequestBody byte[] fileBytes,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("contentType") String contentType){

        return imageFileStoreController.uploadImage(fileBytes, fileName, contentType);
    }
    
    /**======== url查询图片数据 ========= **/
    @GetMapping(value = "parseUrlToBase64")
    public String findImageByUrl(@RequestParam("url") String url){
        return ossService.findImageByUrl(url);
    }
    
    /**======== 根据uniqueId查询图片数据列表 ========= **/
    @GetMapping(value = "findImageListByUniqueId", consumes = "application/json")
    public List<FileUrl> findImageListByUniqueIds(@RequestParam("uniqueIds") List<String> uniqueIds){
        return ossService.findImageListByUniqueIds(uniqueIds);
    }

    /**======== 根据id删除图片数据 ========= **/
    @DeleteMapping(value = "deleteByIds")
    void deleteByIds(@RequestParam("deleteIds") List<String> deleteIds){
        ossService.deleteByIds(deleteIds);
    }

    /**======== 批量上传图片 ========= **/
    @PostMapping(value = "batchUploadImage", consumes = "multipart/form-data")
    void batchUploadImage(@RequestPart("avatarList") List<MultipartFile> avatarList,
                          @RequestParam("uniqueId") String uniqueId){
        ossService.batchUploadImage(avatarList, uniqueId);
    }

    /**======== 根据uniqueId查询图片数据（一张） ========= **/
    @GetMapping(value = "findImageByUniqueId")
    String findImageByUniqueId(@RequestParam("uniqueId") String uniqueId){
        return ossService.findImageByUniqueId(uniqueId);
    }
    
}
