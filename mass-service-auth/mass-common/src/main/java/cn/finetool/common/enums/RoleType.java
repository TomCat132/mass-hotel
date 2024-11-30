package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum RoleType {

    SUPER_ADMIN(0,"super_admin"),
    ADMIN(2,"admin"),
    SYS_ADMIN(1,"sys_admin"),
    USER(3,"user");

    private int code;
    private String key;

    RoleType(int code, String key) {
        this.code = code;
        this.key = key;
    }

}
