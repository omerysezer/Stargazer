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
    private ServerSocket serverSocket;
    private Socket socket;
    HashMap<String, RPiConnection> connections;

    public RPiCommunication(){
        connections = new HashMap<>();
        this.start();
    }

    @Override
    public void run(){
        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedReader reader;
        BufferedWriter writer;
        while(true){
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
                RPiConnection newConnection = new RPiConnection(socket, piToWebBridgeService, this, piSessionId);
                connections.put(piSessionId, newConnection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public RPiConnection getPiWithSessionId(String sessionId){
        return connections.getOrDefault(sessionId, null);
    }

    public void replacePiSessionId(String oldSessionId, String newSessionId) throws IOException {
        RPiConnection connection = connections.get(oldSessionId);
        connections.remove(oldSessionId);
        connections.put(newSessionId, connection);
        connection.changeSessionId(newSessionId);
    }
}

