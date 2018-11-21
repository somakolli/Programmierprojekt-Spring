package de.unistuttgart.Programmierprojekt.Computing;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Tim-Julian Ehret, Julian Blumentröther, Sokol Makolli
 */

@Component
public class Dijkstra {
    private OSMGraph graph;
    private int[] costA;
    Set<Integer> visitedListA;
    private int[] costB;
    Set<Integer> visitedListB;

    private boolean[] visited;
    private int[] previousNodeA;
    private int[] previousNodeB;
    private int srcNode;

    public Dijkstra(){
    }

    public void init(OSMGraph graph) {
        this.graph =graph;
        this.costA = new int[graph.getNoNodes()];
        this.costB = new int[graph.getNoNodes()];
        this.visited = new boolean[graph.getNoNodes()];
        this.previousNodeA = new int[graph.getNoNodes()+1];
        this.previousNodeB = new int[graph.getNoNodes()+1];
        this.srcNode = -1;
        visitedListA = new HashSet<>();
        visitedListB = new HashSet<>();
    }

    public void shortestDistanceA(int src) {
        PriorityQueue<Integer> queue= new PriorityQueue<>(Comparator.comparingInt(o -> costA[o]));
        queue.add(src);
        while (!queue.isEmpty()){
            int currentNode = queue.poll();
            visitedListA.add(currentNode);
            for(int i = graph.getOffset()[currentNode];
                i<graph.getOffset()[currentNode+1]; i++){
                int target =  graph.getTrgtNodes()[i];
                int targetLevel = graph.getLevel()[target];
                int sourceLevel = graph.getLevel()[currentNode];
                if(targetLevel > sourceLevel && costA[graph.getTrgtNodes()[i]]>costA[currentNode]+graph.getCost()[i]){
                    costA[graph.getTrgtNodes()[i]] = costA[currentNode]+graph.getCost()[i];
                    queue.add(graph.getTrgtNodes()[i]);
                    previousNodeA[target] = currentNode;
                }
            }
        }
    }
    public void shortestDistanceB(int src) {
        PriorityQueue<Integer> queue= new PriorityQueue<>(Comparator.comparingInt(o -> costB[o]));
        queue.add(src);
        while (!queue.isEmpty()){
            int currentNode = queue.poll();
            visitedListB.add(currentNode);
            for(int i = graph.getOffset()[currentNode];
                i<graph.getOffset()[currentNode+1]; i++){
                int target =  graph.getTrgtNodes()[i];
                int targetLevel = graph.getLevel()[target];
                int sourceLevel = graph.getLevel()[currentNode];
                if(targetLevel > sourceLevel && costB[graph.getTrgtNodes()[i]]>costB[currentNode]+graph.getCost()[i]){
                    costB[graph.getTrgtNodes()[i]] = costB[currentNode]+graph.getCost()[i];
                    queue.add(graph.getTrgtNodes()[i]);
                    previousNodeB[target] = currentNode;
                }
            }
        }
    }

    public int[] getCost() {
        return costA;
    }

    /**
     * Returns the path if,
     *
     * @param sourceNode , the starting point
     * @param targetNode , the goal Point
     */
    public Integer[] getPath(int sourceNode, int targetNode) {
        ArrayList<Integer> path = new ArrayList<>();
        shortestDistanceA(sourceNode);
        shortestDistanceB(targetNode);
        visitedListA.retainAll(visitedListB);
        int minCost = Integer.MAX_VALUE;
        int v = -1;
        for(Integer i : visitedListA){
            if(costA[i] + costB[i] < minCost)
                minCost = costA[i] + costB[i];
                v = i;
        }
        int currentNodeA = v;
        path.add(currentNodeA);
        while(currentNodeA!=sourceNode) {
            path.add(previousNodeA[currentNodeA]);
            currentNodeA = previousNodeA[currentNodeA];
        }
        int currentNodeB = v;
        path.add(currentNodeB);
        while(currentNodeB!=targetNode) {
            path.add(previousNodeB[currentNodeB]);
            currentNodeB = previousNodeB[currentNodeB];
        }
        return path.toArray(new Integer[0]);
    }
}