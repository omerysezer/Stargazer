package com.omerygouw.stargazer.Repository;

import com.omerygouw.stargazer.Entity.LocationCoordinates;
import com.omerygouw.stargazer.Entity.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends CrudRepository<Session, String> {
    public Session findSessionBySessionId(String sessionId);
}
