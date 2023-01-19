package com.omerygouw.stargazer.Controller;

import com.omerygouw.stargazer.Entity.LocationCoordinates;
import com.omerygouw.stargazer.Entity.ObjectToPointAt;
import com.omerygouw.stargazer.Entity.Response;
import com.omerygouw.stargazer.Service.WebToPiBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    WebToPiBridgeService webToPiBridgeService;

    @MessageMapping("/pointToObject")
    @SendTo("/topic/response")
    public Response pointToObject(ObjectToPointAt object){
        String answer = webToPiBridgeService.instructPiToPointLaserAtObject(object);
        return new Response(answer);
    }

    @MessageMapping("/turnOnLaser")
    @SendTo("/topic/response")
    public Response turnOnLaser(){
        String answer = webToPiBridgeService.instructPiToTurnOnLaser();
        return new Response(answer);
    }

    @MessageMapping("/turnOffLaser")
    @SendTo("/topic/response")
    public Response turnOffLaser(){
        String answer = webToPiBridgeService.instructPiToTurnOffLaser();
        return new Response(answer);
    }

    @MessageMapping("/resetLaser")
    @SendTo("/topic/response")
    public Response resetLaser(){
        String answer = webToPiBridgeService.instructToResetLaserPosition();
        return new Response(answer);
    }


    @MessageMapping("/hello")
    @SendTo("/topic/response")
    public Response saveUserLocation(LocationCoordinates dohicky) {
        return new Response();
    }
}
