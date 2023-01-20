package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.Entity.AstronomicalObject;
import com.omerygouw.stargazer.Entity.LocationCoordinates;
import com.omerygouw.stargazer.Entity.ObjectToPointAt;
import com.omerygouw.stargazer.Entity.UserLocation;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebToPiBridgeService {
    @Autowired
    CoordinateService coordinateService;
    @Autowired
    RPiCommunication rPiCommunicator;
    @Autowired
    UserLocation userLocation;
    public String instructPiToPointLaserAtObject(ObjectToPointAt objectToPointTo){
        if(!rPiCommunicator.theRaspberryPiIsConnected()){
            return "The raspberry pi is not yet connected";
        }

        // TODO: Check plane service to make sure that there are no planes in the area currently

        AstronomicalObject astronomicalObject;
        try{
            astronomicalObject = coordinateService.findObjectCoordinates(objectToPointTo);
        }
        catch (Exception e){
            return "Fail: " + e.getMessage();
        }

        try{
            return rPiCommunicator.instructToPointToObject(astronomicalObject);
        }
        catch(Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String instructPiToTurnOnLaser(){
        if(!rPiCommunicator.theRaspberryPiIsConnected()){
            return "The raspberry pi is not yet connected";
        }

        // TODO: Check plane service to make sure that there are no planes in the area currently

        try{
            return rPiCommunicator.instructToTurnOnLaser();
        }
        catch (Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String instructPiToTurnOffLaser(){
        if(!rPiCommunicator.theRaspberryPiIsConnected()){
            return "The raspberry pi is not yet connected";
        }

        try{
            return rPiCommunicator.instructToTurnOffLaser();
        }
        catch (Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String instructToResetLaserPosition(){
        if(!rPiCommunicator.theRaspberryPiIsConnected()){
            return "The raspberry pi is not yet connected";
        }

        try{
            return rPiCommunicator.instructToResetLaserPosition();
        }
        catch(Exception e){
            return "Fail: Could not send message to raspberry pi";
        }
    }

    public String saveUserLocation(LocationCoordinates location){
        try{
            userLocation.saveUserLocation(location);
            return "Success";
        }
        catch (Exception e){
            return "Fail: " + e.getMessage();
        }
    }
}