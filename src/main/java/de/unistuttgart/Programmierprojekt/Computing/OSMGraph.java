package de.unistuttgart.Programmierprojekt.Computing;

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
    private float[] lat;
    private float[] lon;
    private int[] cost;
    private int[] offset;

    private SimpMessagingTemplate template;

    @Autowired
    public OSMGraph(SimpMessagingTemplate template){
        this.template = template;
    }

    public void loadFromFile(String path) throws Exception{
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
        nodeId = new String[noNodes];
        lat = new float[noNodes];
        lon = new float[noNodes];
        trgtNodes = new int[noEdges];
        cost = new int[noEdges];
        srcNodes = new int[noEdges];
        offset = new int[noNodes+1];
        //load node ids
        for(int i = 0; i<noNodes; i++){
            String line = br.readLine();
            String[] values = line.split(" ");
            nodeId[i] = values[1];
            lat[i] = Float.parseFloat(values[2]);
            lon[i] = Float.parseFloat(values[3]);
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
}