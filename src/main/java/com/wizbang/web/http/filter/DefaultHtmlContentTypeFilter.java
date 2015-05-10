package com.wizbang.web.http.filter;

import javax.ws.rs.Produces;
import javax.ws.rs.container.*;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public final class DefaultHtmlContentTypeFilter implements ContainerResponseFilter {

    public static final DynamicFeature FEATURE = new DynamicFeature() {
        public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
            if (!resourceInfo.getResourceMethod().isAnnotationPresent(Produces.class)
                    && !resourceInfo.getResourceClass().isAnnotationPresent(Produces.class)) {
                featureContext.register(new DefaultHtmlContentTypeFilter());
            }
        }
    };

    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        MultivaluedMap<String, Object> headers = containerResponseContext.getHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.putSingle(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML);
        }
    }
}
