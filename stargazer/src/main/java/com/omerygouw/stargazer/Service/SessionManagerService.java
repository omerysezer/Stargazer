package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Entity.LocationCoordinates;
import com.omerygouw.stargazer.Entity.Session;
import com.omerygouw.stargazer.Repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionManagerService {

    @Autowired
    SessionRepository sessionRepository;

    public Session getSessionById(String sessionId){
        return sessionRepository.findSessionBySessionId(sessionId);
    }

    public void updateUserCoordinates(String sessionId, LocationCoordinates userCoordinates){
        Session session = sessionRepository.findSessionBySessionId(sessionId);
        session.setCoordinates(userCoordinates);
        sessionRepository.save(session);
    }

    public LocationCoordinates getUserCoordinates(String sessionId){
        Session session = sessionRepository.findSessionBySessionId(sessionId);
        return session.getCoordinates();
    }

    public Session createNewSession(String sessionId){
        Session session = new Session();
        session.setSessionId(sessionId);
        sessionRepository.save(session);
        return session;
    }
}
