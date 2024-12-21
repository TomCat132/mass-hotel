package cn.finetool.rabbitmq.configuration;

import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;

public class TrustedClassMapper extends DefaultJackson2JavaTypeMapper {
    public TrustedClassMapper() {
        super();
        addTrustedPackages("*");
    }
}