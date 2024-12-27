package cn.finetool.common.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SystemUtil {

    public static String getOperatingSystem() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)){
            HttpServletRequest request = attributes.getRequest();
            String userAgent = request.getHeader("User-Agent");
            if (userAgent == null) {
                return "Unknown";
            }

            if (userAgent.toLowerCase().contains("windows")) {
                return "Windows";
            } else if (userAgent.toLowerCase().contains("mac")) {
                return "Mac";
            } else if (userAgent.toLowerCase().contains("x11")) {
                return "Unix";
            } else if (userAgent.toLowerCase().contains("android")) {
                return "Android";
            } else if (userAgent.toLowerCase().contains("iphone")) {
                return "iOS";
            } 
        }
        return "Unknown";
    }
}
