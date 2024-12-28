package cn.finetool.common.dto;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AvatarDto implements java.io.Serializable {

    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 即将上传的图片文件列表（不包含已存在的）
     */
    private List<MultipartFile> avatarList;

    /**
     * 删除的图片列表 tb_file_url id 字段
     */
    private List<String> deleteIds;
}
