package com.example.circleapp;

import javafx.scene.paint.Color;

public record Dot(double x, double y, double r, Color color) {

    public static String toMessage(Dot dot){
        String hex = String.format("#%02X%02X%02X",
                (int)(dot.color().getRed() * 255),
                (int)(dot.color().getGreen() * 255),
                (int)(dot.color().getBlue() * 255));
        return dot.x() + ";" + dot.y() + ";" + dot.r() + ";" + hex;
    }

    public static Dot fromMessage(String message){
        String[] parts = message.split(";");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double r = Double.parseDouble(parts[2]);
        Color color = Color.web(parts[3]);
        return new Dot(x, y, r, color);
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double r() {
        return r;
    }

    @Override
    public Color color() {
        return color;
    }
}

