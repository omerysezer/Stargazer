package com.omerygouw.stargazer.Controller;
import com.omerygouw.stargazer.Entity.LongAndLat;
import com.omerygouw.stargazer.Entity.Message;
import com.omerygouw.stargazer.Entity.Response;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {


    @MessageMapping("/hello")
    @SendTo("/topic/response")
    public Response respond(Message name){
        return new Response(name.getName());
    }

    @MessageMapping("/hello")
    @SendTo("/topic/response")
    public LongAndLat save(LongAndLat dohicky){
        bridgeService.saveLocation(dohicky);
        return "Saved";
    }



}
