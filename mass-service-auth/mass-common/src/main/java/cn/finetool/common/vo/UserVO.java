package cn.finetool.common.vo;

import java.util.Map;
import lombok.Data;

@Data
public class UserVO implements java.io.Serializable {


    /**
     * 用户编号
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户邮箱地址
     */
    private String email;

    /**
     * 用户电话号码
     */
    private String phone;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 账号状态
     */
    private Integer status;
}
