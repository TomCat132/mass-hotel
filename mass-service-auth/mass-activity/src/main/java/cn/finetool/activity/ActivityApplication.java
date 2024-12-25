package cn.finetool.activity;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.finetool.api")
@EnableScheduling
@Slf4j
@MapperScan(basePackages = {"cn.finetool.api.mapper", "cn.finetool.activity.mapper"})
@ComponentScan(basePackages = {"cn.finetool.api.handler", "cn.finetool.activity", "cn.finetool.common"})
@SpringBootApplication(scanBasePackages = {"cn.finetool.api", "cn.finetool.activity", "cn.finetool.common"})
public class ActivityApplication {
    public static void main(String[] args) {
       SpringApplication.run(ActivityApplication.class, args);
    }


}
