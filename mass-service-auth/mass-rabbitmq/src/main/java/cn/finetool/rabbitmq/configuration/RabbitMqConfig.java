package cn.finetool.rabbitmq.configuration;


import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.MqRoutingKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", MqExchange.RECHARGE_PLAN_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", MqRoutingKey.RECHARGE_PLAN_DLX_ROUTING_KEY);
        log.info("创建充值方案(活动)队列");
        return new Queue(MqQueue.RECHARGE_PLAN_QUEUE, true, false, false, args);
    }


    /**============= 充值方案(活动) 死信队列 ============== */
    @Bean
    public Queue RechargeDlxQueue() {
        log.info("创建充值方案(活动)死信队列");
        return new Queue(MqQueue.RECHARGE_PLAN_DLX_QUEUE, true, false, false);
    }


    /**============= 充值方案(活动) 死信交换机 ============== */
    @Bean
    public DirectExchange RechargeDlxExchange() {
        log.info("创建充值方案(活动)死信交换机");
        return new DirectExchange(MqExchange.RECHARGE_PLAN_DLX_EXCHANGE);
    }


    /**============= 充值方案(活动) 死信队列绑定到死信交换机 ============== */
    @Bean
    public Binding DlxRechargeBinding() {
        return BindingBuilder.bind(RechargeDlxQueue())
                .to(RechargeDlxExchange())
                .with(MqRoutingKey.RECHARGE_PLAN_DLX_ROUTING_KEY);
    }

    /**============= 充值方案(活动) 交换机 ============== */
    @Bean
    public DirectExchange RechargeExchange() {
        log.info("创建充值方案(活动)交换机");
        return new DirectExchange(MqExchange.RECHARGE_PLAN_EXCHANGE);
    }

    /**============= 充值方案(活动) 队列绑定到 充值方案(活动) 交换机 ============== */
    @Bean
    public Binding RechargeBinding() {
        return BindingBuilder.bind(RechargeQueue())
                .to(RechargeExchange())
                .with(MqRoutingKey.RECHARGE_PLAN_ROUTING_KEY);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("RabbitMqConfig 注入完成");
        // 声明 充值方案(活动) 队列、交换机、绑定
        amqpAdmin.declareQueue(RechargeQueue());
        amqpAdmin.declareQueue(RechargeDlxQueue());
        amqpAdmin.declareExchange(RechargeExchange());
        amqpAdmin.declareExchange(RechargeDlxExchange());
        amqpAdmin.declareBinding(BindingBuilder.bind(RechargeQueue()).to(RechargeExchange()).with(MqRoutingKey.RECHARGE_PLAN_ROUTING_KEY));
        amqpAdmin.declareBinding(BindingBuilder.bind(RechargeDlxQueue()).to(RechargeDlxExchange()).with(MqRoutingKey.RECHARGE_PLAN_DLX_ROUTING_KEY));
    }
}
