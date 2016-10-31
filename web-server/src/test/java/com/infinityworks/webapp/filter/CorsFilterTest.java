package com.infinityworks.webapp.filter;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class CorsFilterTest {

    private HttpServletRequest req;
    private HttpServletResponse res;
    private FilterChain chain;

    @Before
    public void setUp() throws Exception {
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    public void allowsTheHostIfDefined() throws Exception {
        given(req.getHeader("Origin")).willReturn("http://135.31.22.221");
        CorsConfig allowedHosts = new CorsConfig(ImmutableSet.of("http://135.31.22.221"), "");
        CorsFilter underTest = new CorsFilter(allowedHosts);

        underTest.doFilter(req, res, chain);

        verify(res, times(1)).setHeader("Access-Control-Allow-Origin", "http://135.31.22.221");
    }

    @Test
    public void deniesTheHostIfNotRegistered() throws Exception {
        given(req.getHeader("Origin")).willReturn("http://111.11.11.111");
        CorsConfig allowedHosts = new CorsConfig(ImmutableSet.of("http://135.31.22.221"), "");
        CorsFilter underTest = new CorsFilter(allowedHosts);

        underTest.doFilter(req, res, chain);

        verify(res, times(0)).setHeader("Access-Control-Allow-Origin", "http://111.11.11.111");
    }

    @Test
    public void allowsAnyHostIfWildcard() throws Exception {
        given(req.getHeader("Origin")).willReturn("http://111.11.11.111");
        CorsConfig allowedHosts = new CorsConfig(ImmutableSet.of("*"), "");
        CorsFilter underTest = new CorsFilter(allowedHosts);

        underTest.doFilter(req, res, chain);

        verify(res, times(1)).setHeader("Access-Control-Allow-Origin", "http://111.11.11.111");
    }
}