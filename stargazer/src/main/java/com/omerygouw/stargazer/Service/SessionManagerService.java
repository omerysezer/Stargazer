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

    public Session getSessionByClientId(String clientSessionId){
        return sessionRepository.findSessionByClientSessionId(clientSessionId);
    }

    public Session getSessionByPiId(String piSessionId){
        return sessionRepository.findSessionByPiSessionId(piSessionId);
    }

    public void createNewClientSession(String clientSessionId){
        Session newSession = new Session();
        newSession.setClientSessionId(clientSessionId);
        sessionRepository.save(newSession);
    }

    public void updateUserCoordinates(String clientSessionId, LocationCoordinates userCoordinates){
        Session session = sessionRepository.findSessionByClientSessionId(clientSessionId);
        session.setCoordinates(userCoordinates);
        sessionRepository.save(session);
    }

    public LocationCoordinates getUserCoordinates(String clientSessionId){
        Session session = sessionRepository.findSessionByClientSessionId(clientSessionId);
        return session.getCoordinates();
    }

    public void findSessionByClientIdAndAddPiId(String clientId, String piId){
        Session session = sessionRepository.findSessionByClientSessionId(clientId);
        session.setPiSessionId(piId);
        sessionRepository.save(session);
    }
}
