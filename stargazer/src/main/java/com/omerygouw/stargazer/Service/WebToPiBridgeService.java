package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebToPiBridgeService {
    @Autowired
    private CoordinateService coordinateService;
    @Autowired
    private RPiCommunication rPiCommunicator;
    @Autowired
    private SessionManagerService sessionManagerService;

    public String instructPiToPointLaserAtObject(ObjectToPointAt objectToPointTo, String clientSessionId){
       // TODO: Check plane service to make sure that there are no planes in the area currently

        AstronomicalObject astronomicalObject;
        Session session = sessionManagerService.getSessionByClientId(clientSessionId);
        LocationCoordinates userLocation = session.getCoordinates();
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(session.getPiSessionId());

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

    public String instructPiToTurnOnLaser(String clientSessionId){
        // TODO: Check plane service to make sure that there are no planes in the area currently

        Session session = sessionManagerService.getSessionByClientId(clientSessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(session.getPiSessionId());
        try{
            return rPiConnection.instructToTurnOnLaser();
        }
        catch (Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String instructPiToTurnOffLaser(String clientSessionId){
        Session session = sessionManagerService.getSessionByClientId(clientSessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(session.getPiSessionId());

        try{
            return rPiConnection.instructToTurnOffLaser();
        }
        catch (Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String instructToResetLaserPosition(String clientSessionId){
        Session session = sessionManagerService.getSessionByClientId(clientSessionId);
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(session.getPiSessionId());

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