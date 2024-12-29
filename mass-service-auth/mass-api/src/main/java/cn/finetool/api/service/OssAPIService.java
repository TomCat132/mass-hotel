package cn.finetool.api.service;



import cn.finetool.common.configuration.MultipartSupportConfig;
import cn.finetool.common.po.FileUrl;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@FeignClient(name = "mass-oss-service",path = "/oss/api",configuration = MultipartSupportConfig.class)
public interface OssAPIService {

    @PostMapping(value = "uploadFile")
    String uploadFileToMinio(@RequestBody byte[] fileBytes,
                             @RequestParam("fileName") String fileName,
                             @RequestParam("contentType") String contentType);

    /**======== url查询图片数据 ========= **/
    @GetMapping(value = "parseUrlToBase64")
    String findImageByUrl(@RequestParam("url") String url);

    /**======== 根据uniqueId查询图片数据列表 ========= **/
    @GetMapping(value = "findImageListByUniqueId", consumes = "application/json")
    List<FileUrl> findImageListByUniqueIds(@RequestParam("uniqueIds") List<String> uniqueIds);

    /**======== 根据id删除图片数据 ========= **/
    @DeleteMapping(value = "deleteByIds")
    void deleteByIds(@RequestParam("deleteIds") List<String> deleteIds);

    /**======== 批量上传图片 ========= **/
    @PostMapping(value = "batchUploadImage", consumes = "multipart/form-data")
    void batchUploadImage(@RequestPart("avatarList") List<MultipartFile> avatarList,
                          @RequestParam("uniqueId") String uniqueId);
}
