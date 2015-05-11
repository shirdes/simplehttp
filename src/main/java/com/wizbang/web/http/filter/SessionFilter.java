package com.wizbang.web.http.filter;

import com.wizbang.web.http.session.RequestSession;
import com.wizbang.web.http.session.SessionStorage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// TODO: make this the provider for session also.
public final class SessionFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger log = LogManager.getLogger(SessionFilter.class);

    public static final String SESSION_COOKIE_ID = "JSESSIONID";

    private final ThreadLocal<UUID> sessionIdContext = new ThreadLocal<>();
    private final ThreadLocal<RequestSession> sessionContext = new ThreadLocal<>();

    private final SessionStorage storage;

    public SessionFilter(SessionStorage storage) {
        this.storage = storage;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Optional<UUID> requestedId = extractSessionId(requestContext);
        Optional<Map<String, Object>> existingSession = Optional.empty();
        if (requestedId.isPresent()) {
            existingSession = storage.getSessionContent(requestedId.get());
        }

        UUID sessionId;
        RequestSession session;
        if (existingSession.isPresent()) {
            sessionId = requestedId.get();
            session = RequestSession.fromPersisted(existingSession.get());
        }
        else {
            sessionId = UUID.randomUUID();
            session = RequestSession.newEmpty();
        }

        sessionIdContext.set(sessionId);
        sessionContext.set(session);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        UUID sessionId = sessionIdContext.get();
        sessionIdContext.remove();
        RequestSession session = sessionContext.get();
        sessionContext.remove();

        storage.update(sessionId, session);

        Optional<UUID> requestedId = extractSessionId(requestContext);
        if (requestedId.isPresent() && requestedId.get().equals(sessionId)) {
            return;
        }

        NewCookie cookie = NewCookie.valueOf(sessionId.toString());
        responseContext.getCookies().put(SESSION_COOKIE_ID, cookie);
    }

    private Optional<UUID> extractSessionId(ContainerRequestContext requestContext) {
        Map<String, Cookie> cookies = requestContext.getCookies();
        Cookie cookie = cookies.get(SESSION_COOKIE_ID);
        if (cookie == null) {
            return Optional.empty();
        }

        String value = cookie.getValue();
        UUID id;
        try {
            id = UUID.fromString(value);
        }
        catch (IllegalArgumentException e) {
            log.info("Received request with invalid session cookie value -  " + value);
            return Optional.empty();
        }

        return Optional.of(id);
    }

}
