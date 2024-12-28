package cn.finetool.oss.mapper;

import cn.finetool.common.po.FileUrl;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileUrlMapper extends BaseMapper<FileUrl> {
    
    void batchSave(@Param("fileUrlList") List<FileUrl> fileUrlList);
}
