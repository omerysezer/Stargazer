package com.omerygouw.stargazer.Entity;

import org.apache.catalina.User;
import org.springframework.stereotype.Component;

@Component
public class UserLocation {
    private LocationCoordinates userLocation;
    private boolean userLocationHasBeenSaved;

    public void saveUserLocation(LocationCoordinates location){
        if(userLocationHasBeenSaved){
            throw new RuntimeException("User's location has already been saved.");
        }

        userLocation = location;
        userLocationHasBeenSaved = true;
    }

    public LocationCoordinates getUserLocation(){
        if(!userLocationHasBeenSaved){
            return null;
        }

        return userLocation;
    }
}