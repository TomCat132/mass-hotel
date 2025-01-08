package cn.finetool.common.vo;

import cn.finetool.common.po.Evaluation;
import java.util.List;
import lombok.Data;

/**
 * 评价展示相关信息
 */
@Data
public class EvaluationVO extends Evaluation implements java.io.Serializable {
    
    
    public EvaluationVO(){}
    
    public EvaluationVO(Evaluation evaluation){
        this.setRelationId(evaluation.getRelationId());
        this.setEvaluationId(evaluation.getEvaluationId());
        this.setMerchantId(evaluation.getMerchantId());
        this.setOrderId(evaluation.getOrderId());
        this.setUserId(evaluation.getUserId());
        this.setStar(evaluation.getStar());
        this.setContent(evaluation.getContent());
        this.setCreateTime(evaluation.getCreateTime());
    }

    /**
     * 用户头像 Base64
     */
    private String userAvatar;

    /**
     * 评价图列表 Base64
     */
    private List<String> evaluationAvatarList;

    /**
     * 用户名
     */
    private String username;
}
