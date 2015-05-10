package com.wizbang.web.http;

import com.google.common.util.concurrent.AbstractIdleService;
import com.sun.net.httpserver.HttpServer;
import com.wizbang.web.http.filter.DefaultHtmlContentTypeFilter;
import com.wizbang.web.http.resource.IndexResource;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public final class Server extends AbstractIdleService {

    private HttpServer httpServer;

    @Override
    protected void startUp() throws Exception {
        URI base = UriBuilder.fromUri("http://localhost/").port(8080).build();
        ResourceConfig jersey = new ResourceConfig()
                .register(DefaultHtmlContentTypeFilter.FEATURE)
                .register(new IndexResource());

        httpServer = JdkHttpServerFactory.createHttpServer(base, jersey);
    }

    @Override
    protected void shutDown() throws Exception {
        httpServer.stop(0);
    }
}
