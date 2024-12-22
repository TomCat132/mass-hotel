package cn.finetool.oss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"cn.finetool.oss","cn.finetool.common",
        "cn.finetool.api"})
@EnableFeignClients(basePackages = {"cn.finetool.api"})
@MapperScan(basePackages = {"cn.finetool.api.mapper"})
public class OssApplication {
    public static void main(String[] args) {
        SpringApplication.run(OssApplication.class, args);
    }
}