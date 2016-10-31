package com.infinityworks.webapp.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(CorsFilter.class);
    private final CorsConfig hosts;

    @Autowired
    public CorsFilter(CorsConfig hosts) {
        this.hosts = hosts;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String requestOrigin = request.getHeader("Origin");

        if (isHostAllowed(requestOrigin)) {
            response.setHeader("Access-Control-Allow-Origin", requestOrigin);
        }

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", hosts.getMethods());
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, X-Requested-With");

        chain.doFilter(req, res);
    }

    private boolean isHostAllowed(String origin) {
        if (hosts.getHosts().contains("*") ||
            hosts.getHosts().contains(origin)) {
            return true;
        } else {
            log.debug("Request failed CORS filter. origin={}, allowed={}", origin, hosts);
            return false;
        }
    }

    @Override
    public void destroy() {

    }
}