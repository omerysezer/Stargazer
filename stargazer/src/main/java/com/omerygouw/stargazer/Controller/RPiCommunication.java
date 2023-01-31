package com.omerygouw.stargazer.Controller;

import com.omerygouw.stargazer.Entity.RPiConnection;
import com.omerygouw.stargazer.Service.PiToWebBridgeService;
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

            String piSessionId = null;
            try {
                piSessionId = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                RPiConnection newConnection = new RPiConnection(socket, piToWebBridgeService, this);
                connections.put(piSessionId, newConnection);
                writer.write("SUCCESS");
                writer.flush();
            } catch (IOException e) {
                connections.remove(piSessionId);
            }
        }
    }

    public RPiConnection getPiWithSessionId(String sessionId){
        return connections.getOrDefault(sessionId, null);
    }

    public String replacePiSessionId(String oldSessionId, String newSessionId) throws IOException {
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
}

