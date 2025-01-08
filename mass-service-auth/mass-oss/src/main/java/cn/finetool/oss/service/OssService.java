package cn.finetool.oss.service;

import cn.finetool.common.po.FileUrl;
import cn.finetool.common.util.Response;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface OssService {
    
    /**======== 文件批量存储 ========= **/
    void batchStore(String uniqueId, List<MultipartFile> fileList);
    /**======== 文件单个存储 ========= **/
    String uploadFile(MultipartFile file);
    /**======== 根据URL获取图片 ========= **/
    String findImageByUrl(String url);
    /**======== 根据uniqueId查询图片数据列表 ========= **/
    List<FileUrl> findImageListByUniqueIds(List<String> uniqueIds);
    /**======== 根据Id删除图片数据 ========= **/
    void deleteByIds(List<String> deleteIds);
    /**======== 批量上传图片 ========= **/
    void batchUploadImage(List<MultipartFile> avatarList, String uniqueId);
    /**======== 根据uniqueId查询图片数据列表 ========= **/
    Response findImageListByUniqueId(String uniqueId);
}
