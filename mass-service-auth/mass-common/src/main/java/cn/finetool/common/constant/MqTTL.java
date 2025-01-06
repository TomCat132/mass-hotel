package cn.finetool.common.constant;

public class MqTTL {

    /** ======== 5 分钟 ======== */
    public static final long FIVE_MINUTES = 300000;
    
    /** ======== 60 分钟 ======== + 个随机时间 100ms以内*/ 
    public static final long ONE_HOUR = 3600000 + (long) (Math.random() * 100) ;
}
