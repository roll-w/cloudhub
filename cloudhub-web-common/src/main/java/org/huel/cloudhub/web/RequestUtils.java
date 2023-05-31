package org.huel.cloudhub.web;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
public final class RequestUtils {

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "X-Real-IP",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private static boolean isInvalidIp(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) ||
                ip.equalsIgnoreCase(IPV6_LOCAL) || ip.equalsIgnoreCase(IPV4_LOCAL);
    }

    private static final String IPV6_LOCAL = "0:0:0:0:0:0:0:1";
    private static final String IPV4_LOCAL = "127.0.0.1";

    public static String getRemoteIpAddress(HttpServletRequest request) {
        String ip = null;
        String ipAddresses = null;
        for (String header : HEADERS_TO_TRY) {
            ipAddresses = request.getHeader(header);
            if (!isInvalidIp(ipAddresses)) {
                break;
            }
        }
        if (!isInvalidIp(ipAddresses)) {
            ip = ipAddresses.split(",")[0];
        }
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
        }
        if (IPV6_LOCAL.equals(ip)) {
            return IPV4_LOCAL;
        }
        return ip;
    }

    private RequestUtils() {
    }
}
