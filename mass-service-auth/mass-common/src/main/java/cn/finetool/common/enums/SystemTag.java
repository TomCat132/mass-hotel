package cn.finetool.common.enums;

public enum SystemTag {
    
    SYSTEM_SENDER("10000", "系统消息提醒");
    
    private String code;
    private String desc;
    
    SystemTag(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String code(){
        return this.code;
    }
    
    public String desc(){
        return this.desc;
    }
}
