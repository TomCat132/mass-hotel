package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import lombok.Data;

/**
 * 签到奖励内容表
 */
@Data
@TableName(value = "tb_sign_reward")
public class SignReward implements java.io.Serializable {

    /**
     * 奖励编号
     */
    private String rewardId;
    /**
     * 签到时期 yyyy-MM-dd
     */
    private LocalDate rewardDate;
    /**
     * 建立内容
     */
    private String rewardContent;
}
