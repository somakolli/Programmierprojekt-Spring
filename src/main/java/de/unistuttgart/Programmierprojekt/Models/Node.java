package de.unistuttgart.Programmierprojekt.Models;

public class Node {
    private int id;
    private double lon;
    private double lat;

    public Node(int id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    public int getId() {
        return id;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}
