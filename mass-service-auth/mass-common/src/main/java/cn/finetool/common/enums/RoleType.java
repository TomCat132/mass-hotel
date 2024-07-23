package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum RoleType {

    SUPER_ADMIN(0,"超级管理员"),
    ADMIN(2,"普通管理员"),
    
    USER(3,"普通用户");

    private int code;
    private String key;
    
    
    
    RoleType(int code, String key) {
        this.code = code;
        this.key = key;
    }

}
