package cn.finetool.rabbitmq.configuration;


import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.MqRoutingKey;
import jakarta.annotation.Resource;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.HashMap;
import java.util.Map;



@Slf4j
@Configuration
public class RabbitMqConfig implements CommandLineRunner {

    @Resource
    private AmqpAdmin amqpAdmin;
    
    /** ============ 活动券下架 队列绑定 ========== */
    @Bean
    public Binding VoucherDownBinding(){
        return BindingBuilder.bind(VoucherDownQueue())
                .to(VoucherDownExchange())
                .with(MqRoutingKey.VOUCHER_DOWN_ROUTING_KEY).noargs();
    }
    
    /** ============ 活动券下架 交换机 ========== */
    @Bean
    public CustomExchange VoucherDownExchange(){
        return new CustomExchange(MqExchange.VOUCHER_DOWN_EXCHANGE,"x-delayed-message",
                true,false, Collections.singletonMap("x-delayed-type", "direct"));
    }
    
    /** ============ 活动券下架  队列 ========== */
    @Bean
    public Queue VoucherDownQueue(){
        return new Queue(MqQueue.VOUCHER_DOWN_QUEUE, true, false, false);
    }
    
    /** ============ 活动券上架 路由绑定 ========== */
    @Bean
    public Binding VoucherUpBinding(){
        log.info("创建活动券上架队列绑定到活动券上架交换机");
        return BindingBuilder.bind(VoucherUpQueue())
                .to(VoucherUpExchange())
                .with(MqRoutingKey.VOUCHER_UP_ROUTING_KEY).noargs();
    }
    
    /** ============ 活动券上架 交换机 ========== */
    @Bean
    public CustomExchange VoucherUpExchange(){
        log.info("创建活动券上架延时交换机");
        Map<String,Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MqExchange.VOUCHER_UP_EXCHANGE,"x-delayed-message",
                true,false,args);
    }
    
    /** ============ 活动券上架 队列  ========== */
    @Bean
    public Queue VoucherUpQueue(){
        log.info("创建活动券上架队列");
        return new Queue(MqQueue.VOUCHER_UP_QUEUE, true, false, false);
    }

    /** ============ 房间预定订单 路由绑定 ========== */
    @Bean
    public Binding RoomReserveOrderBinding(){
        return BindingBuilder.bind(RoomReserveOrderQueue())
                .to(RoomReserveOrderExchange())
                .with(MqRoutingKey.ROOM_RESERVE_ORDER_ROUTING_KEY).noargs();
    }

    /** ============ 房间预定订单 队列 ========== */
    @Bean
    public Queue RoomReserveOrderQueue(){
        log.info("创建房间预定订单队列");
        return new Queue(MqQueue.ROOM_RESERVE_ORDER_QUEUE, true, false, false);
    }

    /** ============ 房间预定订单 交换机 ========== */
    @Bean
    public CustomExchange RoomReserveOrderExchange(){
        log.info("创建房间预定订单交换机");
        Map<String,Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MqExchange.ROOM_DATE_RESERVE_ORDER_EXCHANGE,"x-delayed-message",
                true,false,args);
    }


    /** ============充值订单 交换机 ========== */
    @Bean
    public CustomExchange orderExchange(){
        log.info("创建订单交换机");
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MqExchange.ORDER_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /** ============ 充值订单 队列 ========== */
    @Bean
    public Queue orderQueue(){
        log.info("创建订单队列");
        return new Queue(MqQueue.RECHARGE_ORDER_QUEUE, true, false, false);
    }

    /** ============ 充值订单路由键 ========== */
    @Bean
    public Binding orderBinding() {
        log.info("创建订单队列绑定到订单交换机");
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(MqRoutingKey.ORDER_ROUTING_KEY).noargs();
    }




//=======================================================================================================

    /**============= 充值方案(活动) 队列 ============== */
    @Bean
    public Queue RechargeQueue() {
        log.info("创建充值方案(活动)队列");
        return new Queue(MqQueue.RECHARGE_PLAN_QUEUE, true, false, false);
    }


    /**============= 充值方案(活动) 交换机 ============== */
    @Bean
    public CustomExchange RechargeExchange() {
        log.info("创建充值方案(活动)交换机");
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MqExchange.RECHARGE_PLAN_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**============= 充值方案(活动) 队列绑定到 充值方案(活动) 交换机 ============== */
    @Bean
    public Binding RechargeBinding() {
        return BindingBuilder.bind(RechargeQueue())
                .to(RechargeExchange())
                .with(MqRoutingKey.RECHARGE_PLAN_ROUTING_KEY).noargs();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("RabbitMqConfig 注入完成");
        // 声明 充值方案(活动) 队列、交换机、绑定
        amqpAdmin.declareQueue(RechargeQueue());
        amqpAdmin.declareExchange(RechargeExchange());
        amqpAdmin.declareBinding(BindingBuilder.bind(RechargeQueue()).to(RechargeExchange()).with(MqRoutingKey.RECHARGE_PLAN_ROUTING_KEY).noargs());
        // 声明 房间预定订单 队列、交换机、绑定
        amqpAdmin.declareQueue(RoomReserveOrderQueue());
        amqpAdmin.declareExchange(RoomReserveOrderExchange());
        amqpAdmin.declareBinding(BindingBuilder.bind(RoomReserveOrderQueue()).to(RoomReserveOrderExchange()).with(MqRoutingKey.ROOM_RESERVE_ORDER_ROUTING_KEY).noargs());
        // 声明 活动券上架 队列、交换机、绑定
        amqpAdmin.declareQueue(VoucherUpQueue());
        amqpAdmin.declareExchange(VoucherUpExchange());
        amqpAdmin.declareBinding(BindingBuilder.bind(VoucherUpQueue()).to(VoucherUpExchange()).with(MqRoutingKey.VOUCHER_UP_ROUTING_KEY).noargs());
        // 声明 活动券下架 队列、交换机、绑定
        amqpAdmin.declareQueue(VoucherDownQueue());
        amqpAdmin.declareExchange(VoucherDownExchange());
        amqpAdmin.declareBinding(BindingBuilder.bind(VoucherDownQueue()).to(VoucherDownExchange()).with(MqRoutingKey.VOUCHER_DOWN_ROUTING_KEY).noargs());
    }
}
