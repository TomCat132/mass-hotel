package cn.finetool.common.vo;

import lombok.Data;

@Data
public class CpMerchantVO implements java.io.Serializable {

    /**
     * 商户编号
     */
    private String merchantId;

    /**
     * 商户类型
     */
    private Integer merchantType;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户状态
     */
    private Integer merchantStatus;

    /**
     * 商户所在城市
     */
    private String city;

    /**
     * 商户地址
     */
    private String address;

    /**
     * 商户电话
     */
    private String phoneNumber;
}
