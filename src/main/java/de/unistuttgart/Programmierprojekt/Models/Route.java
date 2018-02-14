package de.unistuttgart.Programmierprojekt.Models;

public class Route {
    private double [][] path;
    private int distance;

    public Route(double[][] path, int distance) {
        this.path = path;
        this.distance = distance;
    }

    public double[][] getPath() {
        return path;
    }

    public int getDistance() {
        return distance;
    }
}
