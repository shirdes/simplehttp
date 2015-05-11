package com.wizbang.web.http.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SessionStorage {

    Optional<Map<String, Object>> getSessionContent(UUID sessionId);

    void update(UUID sessionId, RequestSession session);

}
