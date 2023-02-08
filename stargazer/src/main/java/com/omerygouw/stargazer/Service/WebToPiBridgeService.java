package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.DTO.FromPiToServerMessage;
import com.omerygouw.stargazer.DTO.LocationCoordinates;
import com.omerygouw.stargazer.DTO.ObjectToPointAt;
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
    @Autowired
    public PlaneService planeService;

    public void pairClientToRaspPi(String piId, String clientId) throws IOException {
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(piId);
        if(rPiConnection == null){
            throw new RuntimeException("Failed: Could not find Raspberry Pi.");
        }

        String sessionId = clientId + piId;
        sessionManagerService.createNewSession(sessionId);

        FromPiToServerMessage result;
        try{
            result = rPiCommunicator.replacePiSessionId(piId, sessionId);
        }
        catch (Exception e){
            sessionManagerService.deleteSessionById(sessionId);
            throw new RuntimeException("Failed: could not provide Raspberry Pi with session id.");
        }

        if(!result.messageType().equals("SUCCESS")){
            sessionManagerService.deleteSessionById(sessionId);
            throw new RuntimeException("Failed: Raspberry Pi failed to accept new session.");
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

        boolean planeInVicinity = planeService.checkPlaneInVicinity(sessionId);
        if(planeInVicinity) {
            throw new RuntimeException(("Failed: Plane(s) in Vicinity"));
        }

        try{
            astronomicalObject = coordinateService.findObjectCoordinates(objectToPointTo, userLocation);
        }
        catch (Exception e){
            throw new RuntimeException("Failed: " + e.getMessage());
        }

        FromPiToServerMessage result;
        try{
            result = rPiConnection.instructToPointToObject(astronomicalObject);
        }
        catch (Exception e){
            throw new RuntimeException("Failed: Could not send message to raspberry pi.");
        }

        if(!result.messageType().equals("SUCCESS")){
            throw new RuntimeException("Failed. \nRaspberry Pi failed to point to \"" + objectToPointTo.objectName() + "\"");
        }
    }

    public void instructPiToTurnOnLaser(String sessionId){
        // TODO: Check plane service to make sure that there are no planes in the area currently

        Session session = sessionManagerService.getSessionById(sessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        if(rPiConnection == null){
            throw new RuntimeException("Failed: No Raspberry Pi is paired.");
        }

        boolean planeInVicinity = planeService.checkPlaneInVicinity(sessionId);
        if(planeInVicinity) {
            throw new RuntimeException(("Failed: Plane(s) in Vicinity"));
        }

        FromPiToServerMessage result;
        try{
            result = rPiConnection.instructToTurnOnLaser();
        }
        catch (Exception e){
            throw new RuntimeException("Failed: Could not send message to raspberry pi.");
        }

        if(!result.messageType().equals("SUCCESS")){
            throw new RuntimeException("Failed. \nRaspberry Pi failed to turn on laser.");
        }
    }

    public void instructPiToTurnOffLaser(String sessionId){
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        if(rPiConnection == null){
            throw new RuntimeException("Failed: No Raspberry Pi is paired.");
        }

        FromPiToServerMessage result;
        try{
            result = rPiConnection.instructToTurnOffLaser();
        }
        catch (Exception e){
            throw new RuntimeException("Failed: Could not send message to raspberry pi.");
        }

        if(!result.messageType().equals("SUCCESS")){
            throw new RuntimeException("Failed. \nRaspberry Pi failed to turn off laser.");
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