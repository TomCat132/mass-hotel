package cn.finetool.oss.handler;

import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.FileUrl;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.oss.mapper.FileUrlMapper;
import cn.finetool.oss.service.OssService;
import cn.finetool.oss.util.FileConvertUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static cn.finetool.common.util.Response.success;

@Component
public class OssHandler implements OssService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OssHandler.class);
    private static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(5, 0);
    @Resource
    private FileUrlMapper fileUrlMapper;
    @Resource
    private Environment config;
    @Autowired
    private MinioClient minioClient;
    @Resource
    private FileConvertUtil fileConvertUtil;

    @Override
    public void batchStore(String uniqueId, List<MultipartFile> fileList) {
        List<FileUrl> fileUrlList = fileList.stream()
                .map(file -> {
                    FileUrl fileUrl = new FileUrl();
                    fileUrl.setId(SysEnum.FILE_PREFIX.code() + ID_WORKER.nextId());
                    fileUrl.setUniqueId(uniqueId);
                    fileUrl.setUrl(uploadFile(file));
                    return fileUrl;
                })
                .collect(Collectors.toList());
        // 批量插入
        fileUrlMapper.batchSave(fileUrlList);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String urlId = SysEnum.FILE_PATH_PREFIX.code() + ID_WORKER.nextId() + "_" + file.getOriginalFilename();
            minioClient.putObject(config.getProperty("minio.bucket"),
                    urlId,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType());

            return config.getProperty("minio.url") + '/' + config.getProperty("minio.bucket") + '/' + urlId;
        } catch (Exception e) {
            throw new BusinessRuntimeException("文件上传失败");
        }
    }

    @Override
    public String findImageByUrl(String url) {
        try {
            return fileConvertUtil.convertFile(url);
        } catch (MalformedURLException e) {
            throw new BusinessRuntimeException("图片加载失败");
        }
    }

    @Override
    public List<FileUrl> findImageListByUniqueIds(List<String> uniqueIds) {
        if (CollectionUtils.isEmpty(uniqueIds)) {
            return Collections.emptyList();
        }
        List<FileUrl> fileUrlList = fileUrlMapper.selectList(new QueryWrapper<FileUrl>()
                .in("unique_id", uniqueIds));
        if (CollectionUtils.isEmpty(fileUrlList)) {
            return Collections.emptyList();
        }
        return fileUrlList.stream()
                .map(fileUrl -> {
                    try {
                        String urlImage = fileConvertUtil.convertFile(fileUrl.getUrl());
                        fileUrl.setUrlImage(urlImage);
                        return fileUrl;
                    } catch (MalformedURLException e) {
                        throw new BusinessRuntimeException("数据异常");
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByIds(List<String> deleteIds) {
        fileUrlMapper.deleteByIds(deleteIds);
    }

    @Override
    public void batchUploadImage(List<MultipartFile> avatarList, String uniqueId) {
        batchStore(uniqueId, avatarList);
    }

    @Override
    public Response findImageListByUniqueId(String uniqueId) {
        return success(findImageListByUniqueIds(Collections.singletonList(uniqueId)));
    }

    @Override
    public String findImageByUniqueId(String uniqueId) {
        FileUrl fileUrl = fileUrlMapper.selectOne(new QueryWrapper<FileUrl>()
                .eq("unique_id", uniqueId)
                .last("LIMIT 1"));
        try {
            // 避免空指针
            return fileConvertUtil.convertFile(fileUrl.getUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
      
    }
}
