package com.omerygouw.stargazer.Entity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.DTO.*;
import com.omerygouw.stargazer.Service.PiToWebBridgeService;
import com.omerygouw.stargazer.Service.WebToPiBridgeService;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.Socket;

public class RPiConnection extends Thread{
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final PiToWebBridgeService piToWebBridgeService;
    private final RPiCommunication rPiCommunication;
    private String sessionId;

    @Builder
    public RPiConnection(PiToWebBridgeService piToWebBridgeService, RPiCommunication rPiCommunication, BufferedReader reader, BufferedWriter writer, String sessionId) {
        this.piToWebBridgeService = piToWebBridgeService;
        this.rPiCommunication = rPiCommunication;
        this.reader = reader;
        this.writer = writer;
        this.sessionId = sessionId;
        this.start();
    }

    public void run(){
        String receivedMessage;

        while(true){
            try {
                receivedMessage = reader.readLine();
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }

            if(receivedMessage == null){
                rPiCommunication.handleLostConnection(sessionId);
                return;
            }

            FromPiToServerMessage messageFromPi = null;
            try{
                messageFromPi = new Gson().fromJson(receivedMessage, FromPiToServerMessage.class);
            } catch (JsonSyntaxException e){
                FromServerToPiMessage errorResponse = FromServerToPiMessage.builder()
                        .instruction("FIX_INVALID_MESSAGE")
                        .instructionData(receivedMessage)
                        .build();
                try {
                    write(new Gson().toJson(errorResponse));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            if(messageFromPi == null){
                continue;
            }

            String sessionId = messageFromPi.sessionId();
            Message messageForClient = null;

            switch (messageFromPi.messageType()) {
                case "CALIBRATION_WARNING" -> {
                    messageForClient = new Message(Status.CALIBRATION_WARNING, "");
                    break;
                }
                case "ORIENTATION_WARNING" -> {
                    messageForClient = new Message(Status.ORIENTATION_WARNING, "");
                    break;
                }
                case "LEVEL_WARNING" -> {
                    messageForClient = new Message(Status.LEVEL_WARNING, "");
                    break;
                }
                case "CALIBRATION_OK" -> {
                    messageForClient = new Message(Status.CALIBRATION_OK, "");
                    break;
                }
                case "LEVEL_OK" -> {
                    messageForClient = new Message(Status.LEVEL_OK, "");
                    break;
                }
                case "ORIENTATION_OK" -> {
                    messageForClient = new Message(Status.ORIENTATION_OK, "");
                    break;
                }
            }

            if(messageForClient != null){
                piToWebBridgeService.sendMessageToClient(messageForClient, sessionId);
            }
        }
    }

    public FromPiToServerMessage instructToPointToObject(AstronomicalObject astronomicalObject) throws IOException {
        ObjectCoordinates coordinates = ObjectCoordinates.builder()
                .azimuth(astronomicalObject.getAzimuth())
                .altitude(astronomicalObject.getAltitude())
                .build();

        String jsonCoordinatesString = new Gson().toJson(coordinates);

        FromServerToPiMessage message = FromServerToPiMessage.builder()
                .instruction("POINT")
                .instructionData(jsonCoordinatesString)
                .build();

       return sendMessageToPiAndGetResponse(message);
    }

    public FromPiToServerMessage instructToTurnOnLaser() throws IOException {
        FromServerToPiMessage message = FromServerToPiMessage.builder()
                .instruction("LASER_ON")
                .instructionData("")
                .build();

        return sendMessageToPiAndGetResponse(message);
    }

    public FromPiToServerMessage instructToTurnOffLaser() throws IOException {
        FromServerToPiMessage message = FromServerToPiMessage.builder()
        .instruction("LASER_OFF")
        .instructionData("")
        .build();

        return sendMessageToPiAndGetResponse(message);
    }


    public FromPiToServerMessage changeSessionId(String newSessionId) throws IOException {
        FromServerToPiMessage message = FromServerToPiMessage.builder()
            .instruction("CHANGE_SESSION")
            .instructionData(newSessionId)
            .build();

        this.sessionId = newSessionId;

        return sendMessageToPiAndGetResponse(message);
    }

    private FromPiToServerMessage sendMessageToPiAndGetResponse(FromServerToPiMessage message) throws IOException {
        write(new Gson().toJson(message));
        String response = reader.readLine();

        if(response == null){
            rPiCommunication.handleLostConnection(sessionId);
            throw new RuntimeException("Raspberry Pi with id \"" + sessionId + "\" is not connected.");
        }

        return new Gson().fromJson(response, FromPiToServerMessage.class);
    }

    private void write(String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }
}
