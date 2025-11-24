package io.github.prjkmo112.common.funcs;

import jakarta.servlet.http.HttpServletRequest;

public final class HttpRequestFunc {

    public static String getClientIp(HttpServletRequest req) {
        String[] headers = {
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_CLIENT_IP", "X-Real-IP"
        };

        for (String h : headers) {
            String val = req.getHeader(h);
            if (val != null && !val.isEmpty() && !"unknown".equalsIgnoreCase(val)) {
                // if multiple ip, the first one is real.
                return val.split(",")[0].trim();
            }
        }

        String remote = req.getRemoteAddr();
        return remote == null ? "" : remote;
    }

    public static String getUserAgent(HttpServletRequest req) {
        String ua = req.getHeader("User-Agent");
        return ua == null ? "" : ua;
    }

    public static boolean isAjax(HttpServletRequest req) {
        String x = req.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(x);
    }
}
