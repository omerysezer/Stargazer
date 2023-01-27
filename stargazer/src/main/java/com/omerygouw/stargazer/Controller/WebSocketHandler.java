package com.omerygouw.stargazer.Controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.omerygouw.stargazer.Entity.LocationCoordinates;
import com.omerygouw.stargazer.Entity.ObjectToPointAt;
import com.omerygouw.stargazer.Service.SessionManagerService;
import com.omerygouw.stargazer.Service.WebToPiBridgeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Controller
public class WebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private WebToPiBridgeService webToPiBridgeService;
    @Autowired
    private SessionManagerService sessionManagerService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        System.out.println("HELLO JESUS");
    }
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws InterruptedException, IOException {
        System.out.println(textMessage.getPayload());
        String payload = textMessage.getPayload();
        JsonObject jsonPayload = JsonParser.parseString(payload).getAsJsonObject();

        String clientSessionId = jsonPayload.get("sessionId").getAsString();

        if(jsonPayload.has("SaveLocation")){
            saveLocation(jsonPayload, clientSessionId, session);
        }
        else if(jsonPayload.has("PointToObject")){
            saveLocation(jsonPayload, clientSessionId, session);
        }
        else if(jsonPayload.has("LaserOff")){
            System.out.println("TURNING OFF THE LASER");
            turnLaserOff(clientSessionId, session);
        }
        else if(jsonPayload.has("LaserOn")) {
            turnLaserOn(clientSessionId, session);
        }
        else if(jsonPayload.has("ReconnectHandshake")){
            sessionManagerService.mapWebSocketSessionToRealSession(session.getId(), clientSessionId);
        }
    }

    private void pointToObject(JsonObject payload, String clientSessionId, WebSocketSession session) throws IOException {
        payload = payload.get("PointToObject").getAsJsonObject();

        ObjectToPointAt objectToPointAt = new ObjectToPointAt();
        objectToPointAt.setObjectName(payload.get("name").getAsString());
        objectToPointAt.setInsideSolarSystem(payload.get("isInsideSolarSystem").getAsBoolean());


        String response = webToPiBridgeService.instructPiToPointLaserAtObject(objectToPointAt, clientSessionId);
        session.sendMessage(new TextMessage(response));
    }

    public void turnLaserOn(String clientSessionId, WebSocketSession session) throws IOException {
        String response = webToPiBridgeService.instructPiToTurnOnLaser(clientSessionId);
        session.sendMessage(new TextMessage(response));
    }

    public void turnLaserOff(String clientSessionId, WebSocketSession session) throws IOException {
        String response = webToPiBridgeService.instructPiToTurnOffLaser(clientSessionId);
        session.sendMessage(new TextMessage(response));
    }

    private void saveLocation(JsonObject payload, String clientSessionId, WebSocketSession session) throws IOException {
        JsonObject location = payload.getAsJsonObject("SaveLocation");
        double longitude = location.get("Longitude").getAsDouble();
        double latitude = location.get("Latitude").getAsDouble();
        LocationCoordinates userLocation = new LocationCoordinates(longitude, latitude);

        try{
            webToPiBridgeService.saveUserLocation(userLocation, clientSessionId);
        }
        catch (Exception e){
            String responseMessage = e.getMessage();

            if(!responseMessage.startsWith("Fail: ")){
                responseMessage = "Fail: " + responseMessage;
            }

            session.sendMessage(new TextMessage(responseMessage));
            return;
        }

        session.sendMessage(new TextMessage("Success"));
    }
    @GetMapping("/getSessionId")
    public void setCookie(HttpServletResponse response){
        System.out.println("getting cookie");
        Cookie cookie = new Cookie("sessionId", String.valueOf(System.nanoTime()));
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
