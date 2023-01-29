package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebToPiBridgeService {
    @Autowired
    private CoordinateService coordinateService;
    @Autowired
    private RPiCommunication rPiCommunicator;
    @Autowired
    private SessionManagerService sessionManagerService;

    public String pairClientToRaspPi(String piId, String clientId) throws IOException {
        RPiConnection connection = rPiCommunicator.getPiWithSessionId(piId);
        if(connection == null){
            return "Pairing Failed: Pi Does Not Exist";
        }

        String sessionId = clientId + piId;
        sessionManagerService.createNewSession(sessionId);
        rPiCommunicator.replacePiSessionId(piId, sessionId);
        return "hi";
    }

    public String instructPiToPointLaserAtObject(ObjectToPointAt objectToPointTo, String sessionId){
       // TODO: Check plane service to make sure that there are no planes in the area currently

        AstronomicalObject astronomicalObject;
        Session session = sessionManagerService.getSessionById(sessionId);
        LocationCoordinates userLocation = session.getCoordinates();
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        if(userLocation == null){
            throw new RuntimeException("Fail: user location is unknown.");
        }

        try{
            astronomicalObject = coordinateService.findObjectCoordinates(objectToPointTo, userLocation);
        }
        catch (Exception e){
            return "Fail: " + e.getMessage();
        }

        try{
            return rPiConnection.instructToPointToObject(astronomicalObject);
        }
        catch(Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String instructPiToTurnOnLaser(String sessionId){
        // TODO: Check plane service to make sure that there are no planes in the area currently

        Session session = sessionManagerService.getSessionById(sessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);
        try{
            return rPiConnection.instructToTurnOnLaser();
        }
        catch (Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String instructPiToTurnOffLaser(String sessionId){
        Session session = sessionManagerService.getSessionById(sessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        try{
            return rPiConnection.instructToTurnOffLaser();
        }
        catch (Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String instructToResetLaserPosition(String sessionId){
        Session session = sessionManagerService.getSessionById(sessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        try{
            return rPiConnection.instructToResetLaserPosition();
        }
        catch(Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String saveUserLocation(LocationCoordinates location, String clientSessionId){
        sessionManagerService.updateUserCoordinates(clientSessionId, location);
        return "Success";
    }
}