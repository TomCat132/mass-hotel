package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 对象存储文件URL
 */
@Data
@TableName("tb_file_url")
public class FileUrl implements java.io.Serializable {

    @TableId("id")
    private String id;

    /**
     * 唯一标识
     */
    @TableField("unique_id")
    private String uniqueId;
    
    /**
     * 在Minio中存储的地址
     */
    @TableField("url")
    private String url;

    /**
     * 图片数据（url转成成的）
     */
    @TableField(exist = false)
    private String urlImage;
}
