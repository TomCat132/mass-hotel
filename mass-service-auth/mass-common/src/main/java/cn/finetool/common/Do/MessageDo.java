package cn.finetool.common.Do;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MessageDo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String,Object> messageMap;
}
