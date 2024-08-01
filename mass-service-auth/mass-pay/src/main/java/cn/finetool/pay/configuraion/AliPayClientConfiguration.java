package cn.finetool.pay.configuraion;


import com.alipay.api.*;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;



@Configuration
public class AliPayClientConfiguration {

    @Resource
    private Environment config;

    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {
        AlipayConfig alipayConfig = new AlipayConfig();
        // 设置 网关 地址
        alipayConfig.setServerUrl(config.getProperty("alipay.gateway-url"));
        // 设置 应用Id
        alipayConfig.setAppId(config.getProperty("alipay.app-id"));
        // 设置 应用私钥
        alipayConfig.setPrivateKey(config.getProperty("alipay.merchant-private-key"));
        // 设置 请求格式，固定值 JSON
        alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
        // 设置 字符串
        alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
        // 设置 支付宝公钥
        alipayConfig.setAlipayPublicKey(config.getProperty("alipay.alipay-public-key"));
        // 设置 签名类型
        alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);
        // 构造 Client
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        return alipayClient;
    }
}
