package cn.finetool.common.vo;

import java.math.BigDecimal;
import lombok.Data;

/**
 * 会员升级相关数据
 */
@Data
public class UpGradeVO implements java.io.Serializable {

    /**
     * 当前会员等级
     */
    private Integer currentLevel;
    /**
     * 下一会员等级
     */
    private Integer nextLevel;
    /**
     * 升级目标积分（下一会员等级）
     */
    private Integer upgradePoints;
    /**
     * 升级所需积分
     */
    private Integer needPoints;
    /**
     * 当前累计积分
     */
    private Integer points;
    /**
     * 当前账户余额
     */
    private BigDecimal account;
    /**
     * 当前积分百分占比
     */
    private double rechargePercent;
}
