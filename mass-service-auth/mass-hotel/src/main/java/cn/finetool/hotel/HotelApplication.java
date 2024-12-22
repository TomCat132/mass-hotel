package cn.finetool.hotel;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.finetool")
@Slf4j
@SpringBootApplication(scanBasePackages = "cn.finetool")
@EnableScheduling
@ComponentScan(basePackages = "cn.finetool")
@MapperScan(basePackages = {"cn.finetool.api.mapper", "cn.finetool.hotel.mapper"})
public class HotelApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }
}
