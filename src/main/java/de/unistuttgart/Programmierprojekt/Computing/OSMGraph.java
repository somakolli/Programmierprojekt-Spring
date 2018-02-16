package de.unistuttgart.Programmierprojekt.Computing;

import de.unistuttgart.Programmierprojekt.Models.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
/**
 * @author Tim-Julian Ehret, Julian Blumentröther, Sokol Makolli
 */

@Component
public class OSMGraph {
    private boolean graphLoaded = false;
    private int noNodes;
    private int noEdges;
    private String[] nodeId;
    private int[] srcNodes;
    private int[] trgtNodes;
    private double[] lat;
    private double[] lon;
    private int[] cost;
    private int[] offset;

    private SimpMessagingTemplate template;

    @Autowired
    public OSMGraph(SimpMessagingTemplate template){
        this.template = template;
    }

    public void loadFromFile(String path) throws Exception{
        int messageRate = 1000000;

        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        //ignore first 5 lines
        for(int i = 0; i<5; i++){
            br.readLine();
        }
        //load number of nodes
        noNodes = Integer.parseInt(br.readLine());
        //load number of edges
        noEdges = Integer.parseInt(br.readLine());
        //initialize arrays
        lat = new double[noNodes];
        lon = new double[noNodes];
        trgtNodes = new int[noEdges];
        cost = new int[noEdges];
        srcNodes = new int[noEdges];
        offset = new int[noNodes+1];
        //load node ids
        for(int i = 0; i<noNodes; i++){
            String line = br.readLine();
            String[] values = line.split(" ");
            lat[i] = Double.parseDouble(values[2]);
            lon[i] = Double.parseDouble(values[3]);
            if(i%messageRate==0)
            template.convertAndSend("/topic/graphStatus", "Loading nodes: " + i + "/" + (noNodes-1));
        }

        //offset variables
        int o = 0;
        int j = 0;

        //load edges
        for(int i = 0; i<noEdges; i++){
            String line = br.readLine();
            String[] values = line.split(" ");
            srcNodes[i] = Integer.parseInt(values[0]);
            trgtNodes[i] = Integer.parseInt(values[1]);
            cost[i] = Integer.parseInt(values[2]);

            //set offset
            if(i == 0){
                offset[0] = j;
            }
            else{
                for(int k = srcNodes[i]-srcNodes[i-1]; k>0; k--){
                    j++;
                    offset[j] = o;
                }
            }
            o++;
            if(i%messageRate==0)
            template.convertAndSend("/topic/graphStatus", "Loading Edges: " + i + "/" + (noEdges-1));
        }
        offset[noNodes] = o;

        br.close();
        graphLoaded = true;
        System.out.println("Graph Loaded!");

        //send web socket message
        template.convertAndSend("/topic/graphStatus", true);
    }

    public void printNodes(){
        for (int i = 0; i<noNodes; i++){
            System.out.println(i + ":" + nodeId[i]);
        }
    }

    public void printEdges(){
        for (int i = 0; i<noEdges; i++){
            System.out.println(srcNodes[i] + ":" + trgtNodes[i] + ":" + cost[i]);
        }
    }

    public void printOffset(){
        for (int i = 0; i<offset.length; i++){
            System.out.println(i + ":" + offset[i]);
        }
    }

    public Node getClosestNode(double lat, double lon){
        double d = Double.MAX_VALUE;
        int closestNode = 0;
        for (int i = 0; i<getNoNodes(); i++){
            double dI = Math.sqrt(Math.pow(lat-getLat()[i], 2) + Math.pow(lon-getLon()[i], 2));
            if(dI<d){
                d = dI;
                closestNode = i;
            }
        }
        return new Node(closestNode, this.lon[closestNode], this.lat[closestNode]);
    }

    //turns an array with ids to a path with coordinates
    public double[][] idsToCoordinates(Integer[] path) {
        double[][] coPath = new double[path.length][2];
        for (int i = 0; i < path.length; i++) {
            coPath[i] = getCoordinatesFromId(path[i]);
        }
        return coPath;
    }

    public double[] getCoordinatesFromId(int id){
        double[] coordinates = new double[2];
        coordinates[0] = getLon()[id];
        coordinates[1] = getLat()[id];
        return coordinates;
    }



    public int getNoNodes() {
        return noNodes;
    }

    public int getNoEdges() {
        return noEdges;
    }

    public String[] getNodeIds() {
        return nodeId;
    }

    public int[] getSrcNodes() {
        return srcNodes;
    }

    public int[] getTrgtNodes() {
        return trgtNodes;
    }

    public int[] getCost() {
        return cost;
    }

    public int[] getOffset() {
        return offset;
    }

    public boolean isGraphLoaded() {
        return graphLoaded;
    }

    public double[] getLat() {
        return lat;
    }

    public double[] getLon() {
        return lon;
    }
}