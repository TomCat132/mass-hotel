package cn.finetool.pay.controller;


import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.util.Response;
import cn.finetool.pay.service.AliPayService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.Api;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@Api(tags = "支付宝支付")
@RequestMapping("/alipay")
public class AliPayController {

    @Resource
    private Environment config;

    @Resource
    private AliPayService aliPayService;

    /**
     * ======== 支付宝页面调用接口 ========
     */
    @PostMapping("/window")
    public Response tradePay(@RequestBody OrderPayDto orderPayDto) {
        String form = aliPayService.tradePay(orderPayDto);
        return Response.success(form);
    }

    /**
     * ======== 支付宝支付异步回调接口 ========
     */
    @PostMapping("/notify")
    public Response tradeNotify(@RequestParam Map<String, String> params) {
        log.info("支付宝支付回调参数：{}", params);
        String result = "failure";

        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params,
                    config.getProperty("alipay.alipay-public-key"),
                    AlipayConstants.CHARSET_UTF8,
                    AlipayConstants.SIGN_TYPE_RSA2);
        } catch (AlipayApiException e) {
            throw new BusinessRuntimeException(BusinessErrors.ALI_PAY_ERROR, e.getMessage());
        }
        if (!signVerified) {
            return Response.error(BusinessErrors.ALI_PAY_VERIFY_ERROR.getCode(),
                    BusinessErrors.ALI_PAY_VERIFY_ERROR.getMsg());
        }
        // 验证成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
        result = aliPayService.VerifyOrder(params);

        return Response.success(result);
    }

}
