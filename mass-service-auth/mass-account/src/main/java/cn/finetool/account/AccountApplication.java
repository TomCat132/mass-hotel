package cn.finetool.account;

import cn.finetool.common.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
@EnableAsync
@EnableFeignClients(basePackages = "cn.finetool.api")
@MapperScan(basePackages = {"cn.finetool.api.mapper", "cn.finetool.account.mapper"})
@ComponentScan(basePackages = {"cn.finetool.account", "cn.finetool.api", "cn.finetool.common"},
excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "cn.finetool.hotel.handler"))
public class AccountApplication {
    
    public static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(0, 0);
    
    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }
}