package com.wizbang.web.http.session;

import com.wizbang.web.http.filter.SessionFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RequestSession implements Session {

    private final Map<String, Object> content;
    private final boolean copyOfPersisted;

    private boolean dirty = false;

    public static RequestSession fromPersisted(Map<String, Object> content) {
        return new RequestSession(new HashMap<>(content), true);
    }

    public static RequestSession newEmpty() {
        return new RequestSession(new HashMap<>(), false);
    }

    private RequestSession(Map<String, Object> content, boolean copyOfPersisted) {
        this.content = content;
        this.copyOfPersisted = copyOfPersisted;
    }

    @Override
    public <T> Optional<T> get(String key) {
        Object o = content.get(key);
        if (o == null) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        T t = (T) o;
        return Optional.of(t);
    }

    @Override
    public void put(String key, Object o) {
        dirty = true;
        content.put(key, o);
    }

    public boolean isCopyOfPersisted() {
        return copyOfPersisted;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Map<String, Object> getContent() {
        return content;
    }
}
