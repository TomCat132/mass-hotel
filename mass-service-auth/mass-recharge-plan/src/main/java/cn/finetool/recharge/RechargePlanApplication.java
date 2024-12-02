package cn.finetool.recharge;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
@EnableFeignClients(basePackages = "cn.finetool")
@ComponentScan(basePackages = {"cn.finetool"})
@MapperScan(basePackages = "cn.finetool.recharge.mapper")
public class RechargePlanApplication {
    public static void main(String[] args) {
        SpringApplication.run(RechargePlanApplication.class, args);
        log.info("RechargePlanApplication started successfully.");
    }


}
