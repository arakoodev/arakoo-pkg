package com.awslambdaconvert.handler;

import java.lang.reflect.Method;

public class Route {
    private final String path;
    private final String httpMethod;
    private final Object controllerBean;
    private final Method method;

    public Route(String path, String httpMethod, Object controllerBean, Method method) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.controllerBean = controllerBean;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public Object getControllerBean() {
        return controllerBean;
    }

    public Method getMethod() {
        return method;
    }
}
