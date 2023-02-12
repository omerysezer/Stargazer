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
    private PlaneService planeService;
    @Autowired
    private MagneticDeclinationService magneticDeclinationService;

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
        AstronomicalObject astronomicalObject;
        Session session = sessionManagerService.getSessionById(sessionId);

        if(session == null){
            throw new RuntimeException("Failed: Could not find session.");
        }

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

        boolean planeInVicinity = planeService.checkPlaneInVicinity(sessionId);
        if(planeInVicinity) {
            throw new RuntimeException("Failed: Plane(s) in Vicinity");
        }

        FromPiToServerMessage result;
        try{
            result = rPiConnection.instructToPointToObject(astronomicalObject);
        }
        catch (Exception e){
            throw new RuntimeException("Failed: Could not send message to raspberry pi.");
        }

        if(!result.messageType().equals("SUCCESS")){
            throw new RuntimeException("Failed: Raspberry Pi failed to point to \"" + objectToPointTo.objectName() + "\"");
        }
    }

    public void instructPiToTurnOnLaser(String sessionId){
        RPiConnection rPiConnection = rPiCommunicator.getPiWithSessionId(sessionId);

        if(rPiConnection == null){
            throw new RuntimeException("Failed: No Raspberry Pi is paired.");
        }

        boolean planeInVicinity = planeService.checkPlaneInVicinity(sessionId);
        if(planeInVicinity) {
            throw new RuntimeException("Failed: Plane(s) in Vicinity");
        }

        FromPiToServerMessage result;
        try{
            result = rPiConnection.instructToTurnOnLaser();
        }
        catch (Exception e){
            throw new RuntimeException("Failed: Could not send message to raspberry pi.");
        }

        if(!result.messageType().equals("SUCCESS")){
            throw new RuntimeException("Failed: Raspberry Pi failed to turn on laser.");
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
            throw new RuntimeException("Failed: Raspberry Pi failed to turn off laser.");
        }
    }

    public void saveUserLocation(LocationCoordinates location, String clientSessionId){
        try{
            sessionManagerService.updateUserCoordinates(clientSessionId, location);
        }
        catch (Exception e){
            throw new RuntimeException("Failed: Could not save your location.");
        }

        RPiConnection connection = rPiCommunicator.getPiWithSessionId(clientSessionId);

        if(connection == null){
            throw new RuntimeException("Failed: No raspberry pi is connected.");
        }

        double magneticDeclination = magneticDeclinationService.getMagneticDeclinationAtLocation(location);

        try{
            sessionManagerService.updateMagneticDeclination(clientSessionId, magneticDeclination);
        }catch (Exception e){
            throw new RuntimeException("Failed: Could not save magnetic declination at your location.");
        }

        FromPiToServerMessage response = null;

        try{
            response = connection.saveMagneticDeclination(magneticDeclination);
        }catch (Exception e){
            throw new RuntimeException("Failed: Could not send magnetic declination to Raspberry Pi.");
        }

        if(!response.messageType().equals("SUCCESS")){
            throw new RuntimeException("Failed: Raspberry Pi failed to save magnetic declination.");
        }
    }
}