package cn.finetool.common.vo;

import lombok.Data;

@Data
public class RoleVO implements java.io.Serializable {

    /**
     * 主键
     */
    private Integer roleId;

    /**
     * 权限名称
     */
    private String roleName;

    /**
     * 权限表示符
     */
    private String roleKey;

    /**
     * 权限赋予人数
     */
    private Integer roleCount;
}
