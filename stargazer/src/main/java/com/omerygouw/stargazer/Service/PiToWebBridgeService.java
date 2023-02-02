package com.omerygouw.stargazer.Service;

import com.omerygouw.stargazer.DTO.Message;
import com.omerygouw.stargazer.DTO.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PiToWebBridgeService {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessageToClient(Message message, String sessionId){
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + sessionId, message);
    }

    public void informClientOfPiDisconnect(String sessionId) {
        Message message = new Message(Status.PI_DISCONNECT, "");
        simpMessagingTemplate.convertAndSend("/user/queue/session-" + sessionId, message);
    }
}