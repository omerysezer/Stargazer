package com.omerygouw.stargazer.Controller;

import com.omerygouw.stargazer.Entity.LocationCoordinates;
import com.omerygouw.stargazer.Entity.ObjectToPointAt;
import com.omerygouw.stargazer.Entity.Response;
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

@Controller
public class WebSocketController {

    @Autowired
    WebToPiBridgeService webToPiBridgeService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/pointToObject")
    public void pointToObject(@Payload ObjectToPointAt object, @Header("sessionId") String clientSessionId){
        String response = webToPiBridgeService.instructPiToPointLaserAtObject(object, clientSessionId);
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, new Response(response));
    }

    @MessageMapping("/turnOnLaser")
    public void turnOnLaser(@Header("sessionId") String clientSessionId){
        String response = webToPiBridgeService.instructPiToTurnOnLaser(clientSessionId);
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, new Response(response));
    }

    @MessageMapping("/turnOffLaser")
    public void turnOffLaser(@Header("sessionId") String clientSessionId){
        String response = webToPiBridgeService.instructPiToTurnOffLaser(clientSessionId);
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, new Response(response));
    }

    @MessageMapping("/resetLaser")
    public void resetLaser(@Header("sessionId") String clientSessionId){
        String response = webToPiBridgeService.instructToResetLaserPosition(clientSessionId);
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, new Response(response));
    }


    @MessageMapping("/bye")
    public void saveUserLocation(LocationCoordinates location, @Header("sessionId") String clientSessionId) {
        String response = webToPiBridgeService.saveUserLocation(location, clientSessionId);
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + clientSessionId, new Response(response));
    }

    @GetMapping("/getSessionId")
    public void setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("sessionId", String.valueOf(System.nanoTime()));
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
