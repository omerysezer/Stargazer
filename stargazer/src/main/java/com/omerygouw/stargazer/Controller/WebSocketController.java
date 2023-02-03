package com.omerygouw.stargazer.Controller;

import com.omerygouw.stargazer.DTO.*;
import com.omerygouw.stargazer.Service.SessionManagerService;
import com.omerygouw.stargazer.Service.WebToPiBridgeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.Semaphore;

@Controller
public class WebSocketController {

    @Autowired
    WebToPiBridgeService webToPiBridgeService;
    @Autowired
    SessionManagerService sessionManagerService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/pointToObject")
    public void pointToObject(@Payload ObjectToPointAt object, @Header("sessionId") String clientSessionId, @Header("messageId") String messageId){
        Message message;
        try{
            webToPiBridgeService.instructPiToPointLaserAtObject(object, clientSessionId);
            message = new Message(Status.SUCCESS, "", messageId);
        }
        catch (Exception e){
            message = new Message(Status.POINT_TO_FAILURE, e.getMessage(), messageId);
        }
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, message);
    }

    @MessageMapping("/turnOnLaser")
    public void turnOnLaser(@Header("sessionId") String clientSessionId, @Header("messageId") String messageId){
        Message message;
        try{
            webToPiBridgeService.instructPiToTurnOnLaser(clientSessionId);
            message = new Message(Status.SUCCESS, "", messageId);
        }
        catch (Exception e){
            message = new Message(Status.LASER_ON_FAILURE, e.getMessage(), messageId);
        }
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, message);
    }

    @MessageMapping("/turnOffLaser")
    public void turnOffLaser(@Header("sessionId") String clientSessionId, @Header("messageId") String messageId){
        Message message;
        try{
            webToPiBridgeService.instructPiToTurnOffLaser(clientSessionId);
            message = new Message(Status.SUCCESS, "", messageId);
        }
        catch (Exception e){
            message = new Message(Status.LASER_OFF_FAILURE, e.getMessage(), messageId);
        }
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, message);
    }

    @MessageMapping("/saveLocation")
    public void saveUserLocation(LocationCoordinates location, @Header("sessionId") String sessionId, @Header("messageId") String messageId) {
        Message message;
        try{
            webToPiBridgeService.saveUserLocation(location, sessionId);
            message = new Message(Status.SUCCESS, "", messageId);
        }
        catch (Exception e){
            message = new Message(Status.LOCATION_SAVE_FAILURE, e.getMessage(), messageId);
        }
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + sessionId, message);
    }

    @MessageMapping("/pair")
    public void pairPi(@Payload SessionIdWrapper piSessionId, @Header("sessionId") String clientSessionId, @Header("messageId") String messageId) {
        Message message;
        try{
            webToPiBridgeService.pairClientToRaspPi(piSessionId.piSessionId(), clientSessionId);
            message = new Message(Status.SUCCESS, "", messageId);
        }
        catch (Exception e){
            message = new Message(Status.PAIRING_FAILURE, e.getMessage(), messageId);
        }

        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, message);
    }

    @GetMapping("/getSessionId")
    public void setCookie(HttpServletResponse response){
        String sessionId = String.valueOf(System.nanoTime());
        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
