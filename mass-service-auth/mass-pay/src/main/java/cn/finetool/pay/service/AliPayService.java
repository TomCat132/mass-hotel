package cn.finetool.pay.service;

import cn.finetool.common.dto.OrderPayDto;

import java.util.Map;

public interface AliPayService {

    String tradePay(OrderPayDto orderPayDto);

    String VerifyOrder(Map<String, String> params);
}
