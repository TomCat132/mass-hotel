package cn.finetool.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;




@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.finetool")
@Slf4j
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
        log.info("OrderApplication started! 端口号: 8090");
    }
}
 
