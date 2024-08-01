package cn.finetool.pay.service.impl;

import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.pay.service.AliPayService;
import cn.finetool.pay.strategy.OrderTypeStrategy;
import cn.finetool.pay.strategy.OrderTypeStrategyFactory;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {


    @Resource
    private AlipayClient alipayClient;

    @Resource
    private Environment config;

    @Resource
    private OrderTypeStrategyFactory orderTypeStrategyFactory;

    @Override
    public String tradePay(OrderPayDto orderPayDto) {

        // 构造支付请求对象
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        JSONObject biz_content = new JSONObject();
        /******必传参数******/
        //商户订单号，商家自定义，保持唯一性
        biz_content.put("out_trade_no",orderPayDto.getOrderType() + "_" + orderPayDto.getOrderId());
        //支付金额
        BigDecimal payAmount = orderPayDto.getUserPayAmount();
        biz_content.put("total_amount", payAmount);
        //订单标题
        biz_content.put("subject", orderPayDto.getSubject());
        biz_content.put("product_code", "QUICK_WAP_WAY");
        /******选填参数******/
        // 自定义参数
        biz_content.put("order_type", orderPayDto.getOrderType());
        // 设置请求参数
        alipayRequest.setBizContent(biz_content.toString());

        // 设置同步通知页面路径
        alipayRequest.setReturnUrl(config.getProperty("alipay.return-url"));
        // 设置异步通知接口路径
        alipayRequest.setNotifyUrl(config.getProperty("alipay.notifyUrl"));

        log.info("biz_content:{}", biz_content);

        // 调用支付接口并获取支付页面
        try {
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest);
            if (response.isSuccess()) {
                log.info("支付页面接口调用成功");
            } else {
                log.info("调用失败");
            }
            return response.getBody();
        } catch (AlipayApiException e) {
            return "支付接口调用失败：" + e.getErrMsg();
        }
    }

    @Override
    public String VerifyOrder(Map<String, String> params) {

        // 查询校验订单是否存在
        String out_trade_no = params.get("out_trade_no");
        String[] parts = out_trade_no.split("_");
        // 判断订单类型
        String orderType = parts[0];
        String orderId = parts[1];
        OrderTypeStrategy strategy = orderTypeStrategyFactory.getStrategy(Integer.valueOf(orderType));
        Object orderInfo = strategy.queryOrder(orderId);
        if (orderInfo == null){
            log.info("订单不存在");
            return "failure";
        }
        String total_amount = params.get("total_amount");
        int totalAmount = new BigDecimal(total_amount).intValue();
        // 校验金额是否一致
        boolean equals = strategy.equalsPayAmount(orderId, totalAmount);
        if (!equals){
            log.info("支付金额不一致");
            return "failure";
        }
        // 判断通知中的 seller_id 是否为 out_trade_no这笔单据对应的操作方
        String sellerId = params.get("seller_id");
        if (!sellerId.equals(config.getProperty("alipay.seller-id"))){
            log.info("商户ID不一致");
            return "failure";
        }
        // 验证 app_id 是否为商户本身
        String appId = params.get("app_id");
        if (!appId.equals(config.getProperty("alipay.app-id"))){
            log.info("应用ID不一致");
            return "failure";
        }
        // 交易通知状态 TRADE_SUCCESS
        String trade_status = params.get("trade_status");
        if (!trade_status.equals("TRADE_SUCCESS")){
            log.info("支付未成功");
            return "failure";
        }
        // 校验订单是否支付
        //!!! 接口的幂等性，需要商户自己保证
        boolean success = strategy.verifyOrderIsPayAmount(orderId);
        if (success){
            log.info("订单已经支付");
            return "success";
        }
        // 订单未支付，处理订单
        strategy.handleOrder(orderId);


        return "success";
    }
}
