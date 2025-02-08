package cn.finetool.common.util;

import cn.finetool.common.enums.Status;

/**
 * 会员机制计算工具类
 */
public class MemberUtil {

    private MemberUtil() {

    }

    // 会员升级机制
    // 最初：青铜会员积分 0
    public static final int MEMBER_BRONZE = 0;
    //白银会员升级积分
    public static final int MEMBER_SILVER = 1000;
    //黄金会员升级积分
    public static final int MEMBER_GOLD = 5000;
    //铂金会员升级积分
    public static final int MEMBER_PLATINUM = 10000;
    //钻石会员升级积分
    public static final int MEMBER_DIAMOND = 20000;

    /**
     * 根据充值累计积分判断是否升级会员: 返回需要升级的会员等级
     */
    public static int handleMemberUpgrade(int oldPoints, int newPoints) {
        if (newPoints >= MEMBER_BRONZE && oldPoints < MEMBER_SILVER) {
            return Status.MEMBER_BRONZE.code();
        } else if (newPoints >= MEMBER_SILVER && oldPoints < MEMBER_GOLD) {
            return Status.MEMBER_SILVER.code();
        } else if (newPoints >= MEMBER_GOLD && oldPoints < MEMBER_PLATINUM) {
            return Status.MEMBER_GOLD.code();
        } else if (newPoints >= MEMBER_PLATINUM && oldPoints < MEMBER_DIAMOND) {
            return Status.MEMBER_PLATINUM.code();
        } else {
            return Status.MEMBER_DIAMOND.code();
        }
    }

    /**
     * 判断是否升级会员
     * @param oldMemberLevel: 老会员等级
     * @param newMemberLevel: 新会员等级
     * @return boolean
     */
    public static boolean isMemberUpgrade(int oldMemberLevel, int newMemberLevel){
        return !Strings.equals(oldMemberLevel, newMemberLevel);
    }

    /**
     * 获取升级所需积分
     * @param memberLevel 会员等级
     * @return int: 积分
     */
    public static int getRelationPoints(int memberLevel){
        if (Strings.equals(memberLevel, Status.MEMBER_SILVER.code())){
            return MEMBER_SILVER;
        } else if (Strings.equals(memberLevel, Status.MEMBER_GOLD.code())){
            return MEMBER_GOLD;
        } else if (Strings.equals(memberLevel, Status.MEMBER_PLATINUM.code())){
            return MEMBER_PLATINUM;
        } else if (Strings.equals(memberLevel, Status.MEMBER_DIAMOND.code())){
            return MEMBER_DIAMOND;
        } else {
            return 0;
        }
    } 
}
