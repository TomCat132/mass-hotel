package cn.finetool.common.configuration;

import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelManager {

    private static final ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    public static void addChannel(String orderId, Channel channel) {
        channelMap.put(orderId, channel);
    }

    public static Channel getChannel(String orderId) {
        return channelMap.get(orderId);
    }

    public static void removeChannel(String orderId) {
        channelMap.remove(orderId);
    }
}
