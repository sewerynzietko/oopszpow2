package com.example.circleapp.client;

import com.example.circleapp.Dot;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ServerThread extends Thread{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Consumer<Dot> dotConsumer;

    public ServerThread(String address, int port) throws IOException{
        this.socket = new Socket(address,port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(),true);
    }

    public void setDotConsumer(Consumer<Dot> consumer){
        this.dotConsumer = consumer;
    }

    public void run(){
        try {
            String line;
            while((line = in.readLine()) != null){
                Dot dot = Dot.fromMessage(line);
                if(dotConsumer != null){
                    dotConsumer.accept(dot);
                }
            }
        } catch (IOException e){
            System.out.println("Błąd połączenia z serwerem.");
        }
    }

    public void send(Dot dot){
        String message = Dot.toMessage(dot);
        out.println(message);
    }

    public void close() throws IOException{
        socket.close();
    }
}
