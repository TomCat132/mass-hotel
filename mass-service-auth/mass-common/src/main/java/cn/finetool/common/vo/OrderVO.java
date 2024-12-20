package cn.finetool.common.vo;

import cn.finetool.common.configuration.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO implements Serializable {

    /**
     * 订单号ID
     */
    private String orderId;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 订单支付金额
     */
    private BigDecimal userPayAmount;

    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 订单用户手机号
     */
    private String phone;

    /**
     * 支付方式
     */
    private Integer payType;

    /**
     * 订单总充值金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单用户姓名
     */
    private String username;

    /**
     * 用户ID
     */
    private String userId;
}
