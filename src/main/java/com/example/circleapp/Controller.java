package com.example.circleapp;

import com.example.circleapp.client.ServerThread;
import com.example.circleapp.server.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class Controller {
    private Server server;
    private ServerThread serverThread;

    @FXML private Canvas canvas;
    @FXML private Slider radiusSlider;
    @FXML private ColorPicker colorPicker;
    @FXML private TextField portField;
    @FXML private TextField addressField;

    private GraphicsContext gc;

    public Controller(){
    }

    public Controller(Server server, ServerThread serverThread){
        this.server = server;
        this.serverThread = serverThread;

        this.serverThread.setDotConsumer(dot -> {
            Platform.runLater(() -> {
                gc.setFill(dot.color());
                gc.fillOval(dot.x()-dot.r(),dot.y()-dot.r(),dot.r()*2,dot.r()*2);
            });
        });
    }

    @FXML
    public void initialize(){
        gc = canvas.getGraphicsContext2D();
    }

    @FXML
    private void onMouseClicked(MouseEvent event){
        Dot dot = new Dot(event.getX(), event.getY(),
                radiusSlider.getValue(), colorPicker.getValue());
        serverThread.send(dot);
    }

    @FXML
    private void onStartServerClicked() {
        try {
            int port = Integer.parseInt(portField.getText());
            String address = addressField.getText();

            server = new Server(port);
            server.start();

            serverThread = new ServerThread(address, port);
            serverThread.setDotConsumer(dot -> {
                Platform.runLater(() -> {
                    gc.setFill(dot.color());
                    gc.fillOval(dot.x() - dot.r(), dot.y() - dot.r(), dot.r() * 2, dot.r() * 2);
                });
            });
            serverThread.start();

        } catch (NumberFormatException e) {
            System.out.println("Błąd w porcie");
        } catch (Exception e) {
            System.out.println("Nie udało się połączyć z serwerem");
        }
    }

    @FXML
    private void onConnectClicked(){
        try {
            int port = Integer.parseInt(portField.getText());
            String address = addressField.getText();

            serverThread = new ServerThread(address, port);
            serverThread.setDotConsumer(dot -> {
                Platform.runLater(() -> {
                    gc.setFill(dot.color());
                    gc.fillOval(dot.x() - dot.r(), dot.y() - dot.r(), dot.r() * 2, dot.r() * 2);
                });
            });
            serverThread.start();

        } catch (NumberFormatException e) {
            System.out.println("Błąd w porcie");
        } catch (IOException e) {
            System.out.println("Nie udało się połączyć z serwerem");
        }
    }

}
