package cn.finetool.hotel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;



@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.finetool")
@Slf4j
@SpringBootApplication(scanBasePackages = "cn.finetool")
public class HotelApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class,args);
    }
}