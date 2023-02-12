package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.DTO.LocationCoordinates;
import com.omerygouw.stargazer.Entity.Session;
import com.omerygouw.stargazer.Repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public void createNewSession(String sessionId){
        Session session = new Session();
        session.setSessionId(sessionId);
        sessionRepository.save(session);
    }

    public void updatePlaneCheck(String sessionId, boolean result, LocalDateTime time){
        Session session = sessionRepository.findSessionBySessionId(sessionId);
        session.setTimeLastCheckedPlane(time);
        session.setLastPlaneCheckResult(result);
        sessionRepository.save(session);
    }

    public void deleteSessionById(String sessionId){
        sessionRepository.deleteById(sessionId);
    }

    public void updateMagneticDeclination(String sessionId, double magneticDeclination) {
        Session session = sessionRepository.findSessionBySessionId(sessionId);
        session.setMagneticDeclination(magneticDeclination);
        sessionRepository.save(session);
    }
}
