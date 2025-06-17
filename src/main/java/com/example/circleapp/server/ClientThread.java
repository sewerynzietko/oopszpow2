package com.example.circleapp.server;

import com.example.circleapp.Dot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Server server;

    public ClientThread(Socket socket, Server server){
        this.socket = socket;
        this.server = server;

        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Błąd połączenia z klientem");
        }
    }

    public void run() {
        try {
            for (Dot dot : server.getSavedDots()) {
                sendMessage(Dot.toMessage(dot));
            }
            String line;
            while ((line = in.readLine()) != null) {
                server.broadcast(line, this);
            }
        } catch (IOException e) {
            System.err.println("Błąd klienta");
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Błąd przy zamykaniu połączenia");
            }
        }
    }


    public void sendMessage(String message) {
        out.println(message);
    }
}
