package cn.finetool.common.enums;


import lombok.Getter;

@Getter
public enum RoomType {

    ECONOMY(0,"经济房"),
    STANDARD(1,"标准房"),
    LUXURY(2,"豪华房"),
    SUITE(3,"总统套房"),
    COUPLE(4,"情侣房"),
    PARENT(5,"亲子房"),
    ;

    //错误码
    private final int code;
    //具体错误信息
    private final String msg;

    RoomType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
