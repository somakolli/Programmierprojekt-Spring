package de.unistuttgart.Programmierprojekt.Computing;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Tim-Julian Ehret, Julian Blumentröther, Sokol Makolli
 */

@Component
public class Dijkstra {
    private OSMGraph graph;
    private int[] cost;
    private boolean[] visited;
    private int[] previousNode;
    private int srcNode;

    public Dijkstra(){
    }

    public void init(OSMGraph graph) {
        this.graph =graph;
        this.cost = new int[graph.getNoNodes()];
        this.visited = new boolean[graph.getNoNodes()];
        this.previousNode = new int[graph.getNoNodes()+1];
        this.srcNode = -1;
    }

    public void shortestPath(int src){
        try {
            shortestPath(src, -1);
        }catch (IndexOutOfBoundsException e){

        }
    }

    public int shortestPath(int src, int trgt) {
        if(trgt!=-1 && this.srcNode==src && visited[trgt]){
            return cost[trgt];
        }else{
            this.srcNode = src;
            for (int i = 0; i<cost.length; i++){
                cost[i] = Integer.MAX_VALUE;
            }
            cost[src] = 0;
        }
        PriorityQueue<Integer> queue= new PriorityQueue<>(Comparator.comparingInt(o -> cost[o]));
        queue.add(src);
        while (!queue.isEmpty()){
            int currentNode = queue.poll();
            visited[currentNode] = true;
            if(currentNode == trgt)return cost[trgt];
            for(int i = graph.getOffset()[currentNode];
                i<graph.getOffset()[currentNode+1]; i++){
                if(cost[graph.getTrgtNodes()[i]]>cost[currentNode]+graph.getCost()[i]){
                    cost[graph.getTrgtNodes()[i]] = cost[currentNode]+graph.getCost()[i];
                    queue.add(graph.getTrgtNodes()[i]);
                    previousNode[graph.getTrgtNodes()[i]] = currentNode;
                }
            }
        }
        return cost[trgt];
    }

    public int[] getCost() {
        return cost;
    }

    /**
     * Returns the path if,
     *
     * @param sourceNode , the starting point
     * @param targetNode , the goal Point
     */
    public Integer[] getPath(int sourceNode, int targetNode) {
        ArrayList<Integer> path = new ArrayList<>();
        if(sourceNode!=this.srcNode){
            shortestPath(sourceNode, targetNode);
        }
        if(!visited[targetNode]){
            shortestPath(sourceNode, targetNode);
        }
        if(cost[targetNode]==Integer.MAX_VALUE){
            return new Integer[0];
        }
        int currentNode = (int) targetNode;
        path.add(currentNode);
        while(currentNode!=sourceNode) {
            path.add(previousNode[currentNode]);
            currentNode = previousNode[currentNode];
        }
        return path.toArray(new Integer[path.size()]);
    }




}