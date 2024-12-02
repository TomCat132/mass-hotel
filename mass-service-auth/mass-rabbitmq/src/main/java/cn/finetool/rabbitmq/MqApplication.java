package cn.finetool.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "cn.finetool.api.service")
@ComponentScan(basePackages = {
        "cn.finetool.rabbitmq.configuration", "cn.finetool.rabbitmq.listener"})
public class MqApplication {
    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class, args);
        log.info("消息队列服务启动成功：5672端口");
    }
}
