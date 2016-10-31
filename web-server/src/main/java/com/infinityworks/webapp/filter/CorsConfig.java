package com.infinityworks.webapp.filter;

import com.google.common.base.MoreObjects;

import java.util.Set;

public class CorsConfig {
    private final Set<String> hosts;
    private final String methods;

    public CorsConfig(Set<String> hosts, String methods) {
        this.hosts = hosts;
        this.methods = methods;
    }

    public String getMethods() {
        return methods;
    }

    public Set<String> getHosts() {
        return hosts;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("hosts", hosts)
                .add("methods", methods)
                .toString();
    }
}
