package cn.finetool.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param object 要转换的对象
     * @return JSON 字符串
     * @throws JsonProcessingException 如果转换失败
     */
    public static String toJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * 将 JSON 字符串转换为对象
     *
     * @param jsonString JSON 字符串
     * @param clazz      要转换的目标对象的类
     * @param <T>        目标对象的类型
     * @return 转换后的对象
     * @throws JsonProcessingException 如果转换失败
     */
    public static <T> T fromJsonString(String jsonString, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, clazz);
    }
}
