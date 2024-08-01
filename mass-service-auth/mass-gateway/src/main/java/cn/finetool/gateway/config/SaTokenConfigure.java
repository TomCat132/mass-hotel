package cn.finetool.gateway.config;


import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class SaTokenConfigure{

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        log.info("注册Sa-Token全局过滤器");
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/user/register")
                .addExclude("/user/login")
                // 支付宝异步通知接口放行
                .addExclude("/alipay/notify")
//                .addExclude("/role/**")
//                .addExclude("/userRoles/**")
//                .addExclude("/rechargePlan/**")
//                .addExclude("/rechargeOrder/**")
                .setAuth(obj -> {
                    // 登录验证
                    SaRouter.match("/**", "/user/login", r -> StpUtil.checkLogin());
                })
                .setError(e -> SaResult.error(e.getMessage()));
    }
}
