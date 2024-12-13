package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * <p>用户表</p>
 * 
 */
@Data
@TableName(value = "tb_user")
public class User implements Serializable {

    /**
     * 用户ID，自增主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @TableField(value = "user_id")
    private String userId;

    /**
     * 用户名
     */

    @TableField(value = "username")
    private String username;

    /**
     * 用户密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 用户邮箱地址
     */

    @TableField(value = "email")
    private String email;

    /**
     * 用户电话号码
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 用户会员等级
     */
    @TableField(value = "member_level")
    private Integer memberLevel;

    /**
     * 用户总积分数量
     */
    @TableField(value = "points")
    private Integer points;

    /**
     * 用户可用积分数
     */
    @TableField(value = "use_points")
    private Integer usePoints;

    /**
     * 用户注册时间
     */
    @TableField(value = "registration_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime registrationTime;

    /**
     * 用户最后登录时间
     */
    @TableField(value = "last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;

    /**
     * 用户头像存储的键
     */
    @TableField(value = "avatar_key")
    private String avatarKey;

    /**
     * 用于加密的盐值
     */
    @TableField(value = "salty")
    private String salty;

    /**
     * 用户账户余额
     */
    @TableField(value = "account")
    private BigDecimal account;

    /**
     * 生成盐
     * @return
     */
    public static String generateSalty(){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
