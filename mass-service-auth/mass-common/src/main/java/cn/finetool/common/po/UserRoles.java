package cn.finetool.common.po;


import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author 胡黄林
 * @since 2024-06-20
 */
@Getter
@Setter
@TableName(value = "sys_user_roles")
public class UserRoles implements Serializable {

    /**
     * id
     */

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 用户Id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 角色Id
     */
    @TableField(value = "role_id")
    private Integer roleId;
}
