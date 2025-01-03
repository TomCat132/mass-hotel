package cn.finetool.common.dto;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class UserDto implements java.io.Serializable {

    /**
     * 用户名
     */
    private String username;
    /**
     * 手机号：唯一
     */
    private String phone;
    /**
     * 角色key
     */
    private String roleKey;
    /**
     * 商户编号
     */
    private String merchantId;
}
