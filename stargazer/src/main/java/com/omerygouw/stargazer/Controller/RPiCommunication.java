package com.omerygouw.stargazer.Controller;

import com.google.gson.Gson;
import com.omerygouw.stargazer.DTO.FromPiToServerMessage;
import com.omerygouw.stargazer.DTO.FromServerToPiMessage;
import com.omerygouw.stargazer.Entity.RPiConnection;
import com.omerygouw.stargazer.Service.PiToWebBridgeService;
import com.omerygouw.stargazer.Service.SessionManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


@Controller
public class RPiCommunication extends Thread {
    @Autowired
    PiToWebBridgeService piToWebBridgeService;
    @Autowired
    SessionManagerService sessionManagerService;
    HashMap<String, RPiConnection> connections;

    public RPiCommunication(){
        connections = new HashMap<>();
        this.start();
    }

    @Override
    public void run(){
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedReader reader;
        BufferedWriter writer;
        while(true){
            Socket socket;
            try {
                socket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            FromServerToPiMessage getIdInstruction = FromServerToPiMessage.builder()
                    .instruction("GIVE_ID")
                    .instructionData("")
                    .build();

            try {
                write(writer, new Gson().toJson(getIdInstruction));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            FromServerToPiMessage invalidMessageResponse = FromServerToPiMessage.builder()
                    .instruction("FIX_INVALID_MESSAGE")
                    .instructionData("")
                    .build();

            String piSessionId = null;
            String piResponse = null;
            try{
                piResponse = reader.readLine();
            }
            catch (Exception e){
                try {
                    write(writer, new Gson().toJson(invalidMessageResponse));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            try{
                FromPiToServerMessage piToServerMessage = new Gson().fromJson(piResponse, FromPiToServerMessage.class);
                piSessionId = piToServerMessage.sessionId();

                if(piSessionId == null){
                    throw new RuntimeException("Null Pi Session Id");
                }
            }catch (Exception e){
                try{
                    write(writer, new Gson().toJson(invalidMessageResponse));
                }catch (Exception ex){
                    throw new RuntimeException(ex);
                }
            }

            try {
                RPiConnection newConnection = RPiConnection.builder()
                        .piToWebBridgeService(piToWebBridgeService)
                        .rPiCommunication(this)
                        .reader(reader)
                        .writer(writer)
                        .sessionId(piSessionId)
                        .build();

                connections.put(piSessionId, newConnection);

                FromServerToPiMessage success = FromServerToPiMessage.builder()
                        .instruction("PROCEED")
                        .instructionData("")
                        .build();

                write(writer, new Gson().toJson(success));
            } catch (IOException e) {
                connections.remove(piSessionId);
            }
        }
    }

    public RPiConnection getPiWithSessionId(String sessionId){
        return connections.getOrDefault(sessionId, null);
    }

    public FromPiToServerMessage replacePiSessionId(String oldSessionId, String newSessionId) {
        RPiConnection connection = connections.get(oldSessionId);
        connections.remove(oldSessionId);
        connections.put(newSessionId, connection);
        try{
            return connection.changeSessionId(newSessionId);
        } catch (Exception e){
            connections.remove(newSessionId);
            connections.put(oldSessionId, connection);
            throw new RuntimeException("Failed: Could not provide pi with new session id.");
        }
    }

    public void handleLostConnection(String sessionId){
        connections.remove(sessionId);
        sessionManagerService.deleteSessionById(sessionId);
        piToWebBridgeService.informClientOfPiDisconnect(sessionId);
    }

    private void write(BufferedWriter writer, String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }
}

