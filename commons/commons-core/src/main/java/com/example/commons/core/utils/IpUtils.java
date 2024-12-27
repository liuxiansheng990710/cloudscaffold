package com.example.commons.core.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpUtils {

    private static final String UNKNOWN_IP = "unknown";

    /**
     * <p>
     * 获取客户端的IP地址的方法是：request.getRemoteAddr()，这种方法在大部分情况下都是有效的。
     * 但是在通过了Apache,Squid等反向代理软件就不能获取到客户端的真实IP地址了，如果通过了多级反向代理的话，
     * X-Forwarded-For的值并不止一个，而是一串IP值， 究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * 例如：X-Forwarded-For：192.168.1.110, 192.168.1.120,
     * 192.168.1.130, 192.168.1.100 用户真实IP为： 192.168.1.110
     * </p>
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        // nginx代理获取的真实用户ip
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        /*
          对于通过多个代理的情况， 第一个IP为客户端真实IP,多个IP按照','分割 "***.***.***.***".length() =
          15
         */
        if ((ip != null && ip.length() > 15) && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }

    /**
     * 网关获取ip
     *
     * @param headers
     * @param request
     * @return
     */
    public static String getIpAddr(HttpHeaders headers, ServerHttpRequest request) {
        // nginx代理获取的真实用户ip
        String ip = headers.getFirst("X-Real-IP");
        if (StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            InetSocketAddress sourceAddress = request.getRemoteAddress();
            if (sourceAddress == null) {
                ip = "";
            } else {
                InetAddress address = sourceAddress.getAddress();
                if (address == null) {
                    //this is unresolved, so we just return the host name
                    //not exactly spec, but if the name should be resolved then a PeerNameResolvingHandler should be used
                    //and this is probably better than just returning null
                    ip = sourceAddress.getHostString();
                } else {
                    ip = address.getHostAddress();

                }
            }

        }
        /*
          对于通过多个代理的情况， 第一个IP为客户端真实IP,多个IP按照','分割 "***.***.***.***".length() =
          15
         */
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

}
