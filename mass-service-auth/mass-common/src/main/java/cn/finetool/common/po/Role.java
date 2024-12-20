package cn.finetool.common.po;


import java.io.Serializable;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * <p>
 * 系统角色表
 * </p>
 */
@Data
@TableName(value = "sys_role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "role_id",type = IdType.AUTO)
    private Integer roleId;

    @TableField(value = "role_name")
    private String roleName;

    @TableField(value = "role_key")
    private String roleKey;
}
