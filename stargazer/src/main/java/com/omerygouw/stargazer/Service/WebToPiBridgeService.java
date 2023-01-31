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

    public void pairClientToRaspPi(String piId, String clientId) throws IOException {
        RPiConnection connection = rPiCommunicator.getPiWithSessionId(piId);
        if(connection == null){
            throw new RuntimeException("Failed: Could not find Raspberry Pi.");
        }

        String sessionId = clientId + piId;
        sessionManagerService.createNewSession(sessionId);

        String result;
        try{
            result = rPiCommunicator.replacePiSessionId(piId, sessionId);
        }
        catch (Exception e){
            throw new RuntimeException("Failed: could not provide Raspberry Pi with session id.");
        }

        if(!result.equals("Success")){
            throw new RuntimeException("Failed: " + result);
        }
    }

    public void instructPiToPointLaserAtObject(ObjectToPointAt objectToPointTo, String sessionId){
       // TODO: Check plane service to make sure that there are no planes in the area currently

        AstronomicalObject astronomicalObject;
        Session session = sessionManagerService.getSessionById(sessionId);
        LocationCoordinates userLocation = session.getCoordinates();
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        if(rPiConnection == null){
            throw new RuntimeException("Failed: No RaspberryPi is paired.");
        }

        if(userLocation == null){
            throw new RuntimeException("Failed: user location is unknown.");
        }

        try{
            astronomicalObject = coordinateService.findObjectCoordinates(objectToPointTo, userLocation);
        }
        catch (Exception e){
            throw new RuntimeException("Failed: " + e.getMessage());
        }

        String result;
        try{
            result = rPiConnection.instructToPointToObject(astronomicalObject);
        }
        catch(Exception e){
            throw new RuntimeException("Failed: Could not instruct Rasperry Pi to point to object.");
        }

        if(!result.equals("Success")){
            throw new RuntimeException("Failed: " + result);
        }
    }

    public void instructPiToTurnOnLaser(String sessionId){
        // TODO: Check plane service to make sure that there are no planes in the area currently

        Session session = sessionManagerService.getSessionById(sessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        if(rPiConnection == null){
            throw new RuntimeException("Failed: No Raspberry Pi is paired.");
        }

        String result;
        try{
            result = rPiConnection.instructToTurnOnLaser();
        }
        catch (Exception e){
            throw new RuntimeException("Failed: Could not send message to raspberry pi.");
        }

        if(!result.equals("Success")){
            throw new RuntimeException("Failed: " + result);
        }
    }

    public void instructPiToTurnOffLaser(String sessionId){
        Session session = sessionManagerService.getSessionById(sessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        if(rPiConnection == null){
            throw new RuntimeException("Failed: No Raspberry Pi is paired.");
        }

        String result;
        try{
            result = rPiConnection.instructToTurnOffLaser();
        }
        catch (Exception e){
            throw new RuntimeException("Failed: Could not send message to raspberry pi");
        }

        if (!result.equals("Success")){
            throw new RuntimeException("Failed: " + result);
        }
    }

    public void saveUserLocation(LocationCoordinates location, String clientSessionId){
        try{
            sessionManagerService.updateUserCoordinates(clientSessionId, location);
        }
        catch (Exception e){
            throw new RuntimeException("Failed: " + e.getMessage());
        }
    }
}