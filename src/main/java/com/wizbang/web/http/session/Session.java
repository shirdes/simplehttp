package com.wizbang.web.http.session;

import java.util.Optional;

public interface Session {

    <T> Optional<T> get(String key);

    void put(String key, Object o);

}
