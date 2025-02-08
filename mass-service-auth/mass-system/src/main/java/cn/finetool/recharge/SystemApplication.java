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
@MapperScan(basePackages = {"cn.finetool.recharge.mapper", "cn.finetool.api.mapper"})
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        log.info("RechargePlanApplication started successfully.");
    }


}
