package com.example.circleapp.server;

import com.example.circleapp.Dot;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends Thread{
    private int port;
    private ServerSocket serverSocket;
    private List<ClientThread> clients = new CopyOnWriteArrayList<>();
    private Connection connection;

    public Server(int port){
        this.port = port;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:circleapp.db");
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("""
        CREATE TABLE IF NOT EXISTS dot(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            x INTEGER NOT NULL,
            y INTEGER NOT NULL,
            color TEXT NOT NULL,
            radius INTEGER NOT NULL);""");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Błąd połączenia z bazą");
        }
    }

    public void run(){
        try{
            serverSocket = new ServerSocket(port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket, this);
                clients.add(clientThread);
                clientThread.start();
            }
        }catch(IOException e){
            System.err.println("Błąd serwera.");
        }
    }

    public void broadcast(String message, ClientThread sClient){
        saveDot(Dot.fromMessage(message));
        for(ClientThread client : clients) {
            if(client != sClient){
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientThread client){
        clients.remove(client);
    }

    public void saveDot(Dot dot) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("""
            INSERT INTO dot(x, y, color, radius) VALUES (?, ?, ?, ?);
        """);
            pstmt.setInt(1, (int) dot.x());
            pstmt.setInt(2, (int) dot.y());
            pstmt.setString(3, toHex(dot.color()));
            pstmt.setInt(4, (int) dot.r());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Błąd zapisu do bazy");
        }
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    public List<Dot> getSavedDots() {
        List<Dot> dots = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT x, y, color, radius FROM dot");
            while (rs.next()) {
                double x = rs.getInt("x");
                double y = rs.getInt("y");
                double r = rs.getInt("radius");
                Color color = Color.web(rs.getString("color"));
                dots.add(new Dot(x, y, r, color));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Błąd odczytu z bazy");
        }
        return dots;
    }

}
