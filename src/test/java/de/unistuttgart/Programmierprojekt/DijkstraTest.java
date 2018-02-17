package de.unistuttgart.Programmierprojekt;

import de.unistuttgart.Programmierprojekt.Computing.Dijkstra;
import de.unistuttgart.Programmierprojekt.Computing.OSMGraph;
import org.assertj.core.api.SoftAssertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
/**
 * @author Tim-Julian Ehret, Julian Blumentröther, Sokol Makolli
 */
public class DijkstraTest {
    private OSMGraph graph = new OSMGraph();
    boolean graphLoaded = false;

    @Test
    public void initiateTest() throws Exception{
        SoftAssertions softly = new SoftAssertions();
        testWithFile("src/test/java/germany.test", softly);
        softly.assertAll();
    }
    private void testWithFile(String path, SoftAssertions softly) throws Exception{
        System.out.println("Testgraph: " + path);
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        Long time = System.currentTimeMillis();
        graph.loadFromFile(br.readLine());
        softly.assertThat(120000>System.currentTimeMillis()-time).isTrue();
        Dijkstra dijkstra = new Dijkstra();
        dijkstra.init(graph);
        String line = br.readLine();
        while (!line.equals("end end")) {
            if (line.equals("src")) {
                System.out.println("One-To-All Test");
                Long timeD = System.currentTimeMillis();
                int sourceNode = Integer.parseInt(br.readLine());
                dijkstra.shortestDistance(sourceNode);
                softly.assertThat(20000 > System.currentTimeMillis() - timeD).isTrue();
                line = br.readLine();
                while (!line.equals("end")) {
                    String[] values = line.split(" ");
                    int trgtNode = Integer.parseInt(values[0]);
                    int expectedCost = Integer.parseInt(values[1]);
                    int actualCost = dijkstra.getCost()[trgtNode];
                    printTestResult(sourceNode,trgtNode,expectedCost,actualCost);
                    softly.assertThat(expectedCost).isEqualTo(actualCost);
                    line = br.readLine();
                }
                line = br.readLine();
            } else if (line.equals("src/trgt")) {
                System.out.println("One-To-One Test");
                line = br.readLine();
                while (!line.equals("end")) {
                    String[] values = line.split(" ");
                    int sourceNode = Integer.parseInt(values[0]);
                    int trgtNode = Integer.parseInt(values[1]);
                    int expectedCost = Integer.parseInt(values[2]);
                    Long timeD = System.currentTimeMillis();
                    int actualCost = dijkstra.shortestDistance(sourceNode, trgtNode);
                    softly.assertThat(20000 > System.currentTimeMillis() - timeD).isTrue();
                    printTestResult(sourceNode,trgtNode,expectedCost,actualCost);
                    softly.assertThat(expectedCost).isEqualTo(actualCost);
                    line = br.readLine();
                }
                line = br.readLine();
            }
        }
    }
    private void printTestResult(int sourceNode, int trgtNode, int expectedCost, int actualCost){
        if(expectedCost!=actualCost){
            System.err.println("Source node: " + sourceNode + " | Traget node: " + trgtNode +
                    " | Expected cost: " + expectedCost + " | Actual cost: " + actualCost);
        }
        else{
            System.out.println("Source node: " + sourceNode + " | Traget node: " + trgtNode +
                    " | Expected cost: " + expectedCost + " | Actual cost: " + actualCost);
        }
    }
}