package cn.finetool.rabbitmq.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MessageDo implements Serializable {

    private Map<String, Object> messageMap;
}
