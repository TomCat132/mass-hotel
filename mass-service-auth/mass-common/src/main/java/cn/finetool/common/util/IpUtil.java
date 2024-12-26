package cn.finetool.common.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class IpUtil {

    private IpUtil() {

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

    /**
     * 获取客户端IP
     * @return
     */
    public static String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            return getClientIpFromRequest(request);
        }
        return "unkown IP";
    }

    /**
     * 获取 IP地址相关信息
     * @param ip
     * @return
     */
    public static JsonNode getIpInfo(String ip) {
        String url = "http://ip-api.com/json/" + ip + "?lang=zh-CN";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            JsonNode rootNode = JsonUtil.fromJsonString(response.toString(), JsonNode.class);
            return rootNode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取 IP地址的经纬度信息
     *
     * @return
     */
    public static Map<String, String> getLatAndLon() {
        String ip = getClientIp();
        JsonNode ipInfo = getIpInfo(ip);
        return ImmutableMap.of("lat", ipInfo.get("lat").asText(),
                "lon", ipInfo.get("lon").asText());
    }
    
    /**
     * 获取用户基本位置信息
     * @return
     */
    public static Map<String, String> getLocationInfo(String ip) {
        JsonNode ipInfo = getIpInfo(ip);
        return ImmutableMap.of(
                "country", ipInfo.get("country").asText(),
                "province", ipInfo.get("regionName").asText(),
                "city", ipInfo.get("city").asText(),
                "lat", ipInfo.get("lat").asText(),
                "lon", ipInfo.get("lon").asText()
        );
    }

    /**
     * 获取用户基本位置信息
     * @return
     */
    public static Map<String, String> getLocationInfo() {
        String ip = getClientIp();
        return getLocationInfo(ip);
    }
    

    public static void main(String[] args) {
        String ip = "182.118.237.164";
        Map<String, String> locationInfo = getLocationInfo(ip);
        System.out.println(locationInfo);
    }
}
