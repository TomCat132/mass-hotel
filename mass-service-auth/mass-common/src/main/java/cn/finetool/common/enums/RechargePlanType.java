package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum RechargePlanType {

    NORMAL(0), // 普通类型

    ACTIVITY(1); // 活动类型

    private final int value;

    RechargePlanType(int value) {
        this.value = value;
    }

}
