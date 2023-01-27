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

    public void createNewClientSessionWithClientIdOnly(String clientSessionId){
        Session newSession = new Session();
        newSession.setClientSessionId(clientSessionId);
        sessionRepository.save(newSession);
    }

    public void createNewClientSessionWithClientAndWebSocketId(String clientSessionId, String websocketSessionId){
        Session newSession = new Session();
        newSession.setWebsocketSessionId(websocketSessionId);
        newSession.setClientSessionId(clientSessionId);
        sessionRepository.save(newSession);
    }

    public void updateUserCoordinates(String clientSessionId, LocationCoordinates userCoordinates){
        Session session = sessionRepository.findSessionByClientSessionId(clientSessionId);
        session.setCoordinates(userCoordinates);
        sessionRepository.save(session);
    }

    public void findSessionByClientIdAndAddPiId(String clientId, String piId){
        Session session = sessionRepository.findSessionByClientSessionId(clientId);
        session.setPiSessionId(piId);
        sessionRepository.save(session);
    }

    public void mapWebSocketSessionToRealSession(String websocketSessionId, String clientSessionId){
        Session session = sessionRepository.findSessionByClientSessionId(clientSessionId);
        session.setWebsocketSessionId(websocketSessionId);
        sessionRepository.save(session);
    }
}
