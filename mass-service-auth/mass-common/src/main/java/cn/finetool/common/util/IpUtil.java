package cn.finetool.common.util;


import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class IpUtil {

    private IpUtil() {

    }

    public static String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            return getClientIpFromRequest(request);
        }
        return "unkown IP";
    }

    private static String getClientIpFromRequest(HttpServletRequest request) {
        // 从请求头中获取 X-Forwarded-For
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 如果 X-Forwarded-For 为空，尝试从 X-Real-IP 获取
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            // 如果 X-Real-IP 也为空，使用 getRemoteAddr
            ip = request.getRemoteAddr();
        }
        // 如果有多个 IP 地址，取第一个非 unknown 的地址
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }


}
