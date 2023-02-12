package com.omerygouw.stargazer.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.omerygouw.stargazer.DTO.LocationCoordinates;
import com.omerygouw.stargazer.Entity.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class PlaneService{
    @Autowired
    SessionManagerService sessionManagerService;

    public Boolean checkPlaneInVicinity(String sessionId){

        Session session = sessionManagerService.getSessionById(sessionId);
        LocationCoordinates userLocation = session.getCoordinates();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowMinusFiveMinutes = LocalDateTime.now().minusMinutes(5);
        LocalDateTime timeLastCheckedPlane = session.getTimeLastCheckedPlane();
        boolean lastPlaneCheckResult = session.isLastPlaneCheckResult();

        if(timeLastCheckedPlane != null && timeLastCheckedPlane.isAfter(nowMinusFiveMinutes)){
            return lastPlaneCheckResult;
        }

        double minLat = userLocation.latitude() - (0.009 * 8.5);
        double maxLat = userLocation.latitude() + (0.009 * 8.5);
        double minLon = userLocation.longitude() - (0.009 * 8.5);
        double maxLon = userLocation.longitude() + (0.009 * 8.5);

        String requestUri = "https://opensky-network.org/api/states/all?lamin="+minLat+"&lomin="+minLon+"&lamax="+maxLat+"&lomax="+maxLon+"";

        WebClient client = WebClient.create();
        WebClient.ResponseSpec responseSpec = client.get()
                .uri(requestUri)
                .retrieve();

        String response = responseSpec.bodyToMono(String.class).block();

        if(response == null){
            throw new RuntimeException("Did not receive response from OpenSky's API.");
        }

        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        boolean planeStatus = !jsonResponse.get("states").isJsonNull();
        sessionManagerService.updatePlaneCheck(sessionId, planeStatus, now);
        return planeStatus;
    }


}
