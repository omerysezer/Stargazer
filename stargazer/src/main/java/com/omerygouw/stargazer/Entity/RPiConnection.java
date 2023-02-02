package com.omerygouw.stargazer.Entity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.omerygouw.stargazer.Controller.RPiCommunication;
import com.omerygouw.stargazer.DTO.*;
import com.omerygouw.stargazer.Service.PiToWebBridgeService;
import lombok.Builder;

import java.io.*;
import java.net.Socket;

public class RPiConnection extends Thread{
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final PiToWebBridgeService piToWebBridgeService;
    private final RPiCommunication rPiCommunication;
    private final Socket socket;
    private String sessionId;
    private boolean isConnected;

    public RPiConnection(Socket socket, PiToWebBridgeService piToWebBridgeService, RPiCommunication rPiCommunication, BufferedReader reader, BufferedWriter writer) {
        this.piToWebBridgeService = piToWebBridgeService;
        this.rPiCommunication = rPiCommunication;
        this.reader = reader;
        this.writer = writer;
        this.socket = socket;
        this.isConnected = true;
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

            FromPiToServerMessage message = null;
            try{
                message = new Gson().fromJson(receivedMessage, FromPiToServerMessage.class);
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

            if(message == null){
                continue;
            }

            if(message.messageType().equals("")){

            }
            else if(message.messageType().equals("1")){

            }
            else if(message.messageType().equals("2")){

            }
            else if(message.messageType().equals("3")){

            }

//            if(receivedMessage.startsWith("Unsolicited:")){
//                receivedMessage = receivedMessage.replaceAll("Unsolicited:", "");
//                String[] split = receivedMessage.split("sessionId:");
//                String warning = split[0];
//                String id = split[1];
//                sessionId = id;
//                Message message;
//
//                if(receivedMessage.startsWith("Bad Calibration:")){
//                    message = new Message(Status.CALIBRATION_WARNING, warning);
//                    piToWebBridgeService.warnBadCalibration(message, id);
//                }
//                else if(receivedMessage.startsWith("Bad Orientation:")){
//                    message = new Message(Status.ORIENTATION_WARNING, warning);
//                    piToWebBridgeService.warnBadOrientation(message, id);
//                }
//            }
        }
    }

    private void throwErrorIfNotConnected(){
        if(!isConnected){
            throw new RuntimeException("Not connected to Raspberry Pi.");
        }
    }
    public String instructToPointToObject(AstronomicalObject astronomicalObject) throws IOException {
        throwErrorIfNotConnected();

        ObjectCoordinates coordinates = ObjectCoordinates.builder()
                .azimuth(astronomicalObject.getAzimuth())
                .altitude(astronomicalObject.getAltitude())
                .build();

        String jsonCoordinatesString = new Gson().toJson(coordinates);

        FromServerToPiMessage message = FromServerToPiMessage.builder()
                .instruction("POINT")
                .instructionData(jsonCoordinatesString)
                .build();


        write(new Gson().toJson(message));
        return reader.readLine();
    }

    public String instructToTurnOnLaser() throws IOException {
        throwErrorIfNotConnected();

        FromServerToPiMessage message = FromServerToPiMessage.builder()
                .instruction("LASER_ON")
                .instructionData("")
                .build();

        write(new Gson().toJson(message));
        return reader.readLine();
    }

    public String instructToTurnOffLaser() throws IOException {
        throwErrorIfNotConnected();

        FromServerToPiMessage message = FromServerToPiMessage.builder()
        .instruction("LASER_OFF")
        .instructionData("")
        .build();

        write(new Gson().toJson(message));
        return reader.readLine();
    }


    public String changeSessionId(String newSessionId) throws IOException {
        throwErrorIfNotConnected();

        FromServerToPiMessage message = FromServerToPiMessage.builder()
            .instruction("CHANGE_SESSION")
            .instructionData(newSessionId)
            .build();

        write(new Gson().toJson(message));
        return reader.readLine();
    }

    private void write(String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }
}
