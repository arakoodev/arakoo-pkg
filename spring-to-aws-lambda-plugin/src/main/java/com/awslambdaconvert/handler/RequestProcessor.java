package com.awslambdaconvert.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(RequestProcessor.class);
    private static final Map<String, Route> routes = new HashMap<>();

    public RequestProcessor() {

        try {
            String applicationStartClass = System.getenv("APPLICATION_START_CLASS");

            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
            ctx.register(Class.forName(applicationStartClass));
            ctx.refresh();

            String[] controllerBeans = ctx.getBeanNamesForAnnotation(RestController.class);
            for (String controllerBeanName : controllerBeans) {
                Object controllerBean = ctx.getBean(controllerBeanName);
                Class<?> classController = controllerBean.getClass();
                // process methods
                for (Method method : classController.getDeclaredMethods()) {

                    Annotation requestMappingMethodAnnotation = method.getAnnotation(RequestMapping.class);

                    // process @RequestMapping
                    if (requestMappingMethodAnnotation != null) {
                        RequestMapping requestMappingAnnotation = (RequestMapping) requestMappingMethodAnnotation;

                        String path = requestMappingAnnotation.value()[0];
                        String httpMethod = requestMappingAnnotation.method()[0].name();

                        routes.put(getRoutingId(path, httpMethod), new Route(path, httpMethod, controllerBean, method));
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to load request processor routes", e);
        }
    }

    public ApiResponse handleRequest(ApiRequest apiRequest) throws InvocationTargetException, IllegalAccessException {
        Object response = null;
        Route route = routes.get(getRoutingId(apiRequest.getPath(), apiRequest.getHttpMethod()));
        if (route == null) {
            return new ApiResponse("not-found", 404);
        }

        Object bean = route.getControllerBean();
        Method method = route.getMethod();

        response = method.invoke(bean);
        return new ApiResponse(response, 200);
    }

    private String getRoutingId(String path, String method) {
        return String.format("%s_%s", path.toLowerCase(), method.toLowerCase());
    }
}
