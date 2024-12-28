package cn.finetool.oss.resources;

import cn.finetool.common.util.Response;
import cn.finetool.oss.service.OssService;
import io.swagger.annotations.Api;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static cn.finetool.common.util.Response.success;

@RestController
@RequestMapping("/oss")
@Api(value = "文件资源相关接口")
public class OssWebSource {
   
    @Resource
    private OssService ossService;
    
    /**
     * 批量存储文件
     * @param uniqueId
     * @param fileList
     * @return
     */
    @PostMapping("/store")
    public Response batchStore(@RequestParam("uniqueId") String uniqueId, 
                          @RequestParam("fileList") List<MultipartFile> fileList){
        ossService.batchStore(uniqueId, fileList);
        return success("上传成功");
    }
}
