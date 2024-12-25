package cn.finetool.order;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@Slf4j
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.finetool")
@MapperScan(basePackages = {"cn.finetool.api.mapper", "cn.finetool.order.mapper"})
@ComponentScan(basePackages = {"cn.finetool"})
@SpringBootApplication(scanBasePackages = {"cn.finetool.common", "cn.finetool.api"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
        log.info("OrderApplication started! 端口号: 8090");
    }
}
 
