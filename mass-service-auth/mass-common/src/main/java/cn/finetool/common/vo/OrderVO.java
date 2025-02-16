package cn.finetool.common.vo;

import cn.finetool.common.configuration.CustomLocalDateTimeDeserializer;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.HeadFontStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontHeightInPoints = 12, fontName = "微软雅黑")
public class OrderVO implements Serializable {

    /**
     * 订单用户姓名
     */
    @ExcelProperty("购买用户")
    @ColumnWidth(15)
    private String username;
    
    /**
     * 订单号ID
     */
    @ExcelProperty("订单号")
    @ColumnWidth(30)
    private String orderId;

    /**
     * 订单类型
     */
    @ExcelProperty("订单类型")
    private String orderType;

    /**
     * 订单状态
     */
    private Integer orderStatus;
    


    /**
     * 订单支付金额
     */
    @ExcelProperty("订单支付金额")
    @ColumnWidth(15)
    private BigDecimal userPayAmount;

    @ExcelProperty("订单状态")
    @ColumnWidth(15)
    private String orderStatusValue;

    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ExcelProperty("订单创建时间")
    @DateTimeFormat("yyyy年MM月dd日 HH时mm分ss秒")
    @ColumnWidth(30)
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
     * 用户ID
     */
    private String userId;

    /**
     * 订单是否评价
     */
    private Integer isEvaluate;
}
