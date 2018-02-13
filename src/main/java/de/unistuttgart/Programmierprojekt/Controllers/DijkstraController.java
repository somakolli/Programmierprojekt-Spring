package de.unistuttgart.Programmierprojekt.Controllers;

import de.unistuttgart.Programmierprojekt.Computing.Dijkstra;
import de.unistuttgart.Programmierprojekt.Computing.OSMGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DijkstraController {

    private Dijkstra dijkstra;
    private OSMGraph osmGraph;

    @Autowired
    public DijkstraController(Dijkstra dijkstra, OSMGraph osmGraph){
        this.dijkstra = dijkstra;
        this.osmGraph = osmGraph;
    }

    @RequestMapping("/distance")
    public int distance(@RequestParam int src, @RequestParam int trgt) throws Exception{
        if(!osmGraph.isGraphLoaded()) throw new Exception();
        return dijkstra.shortestPath(src, trgt);
    }
    @RequestMapping("/path")
    public String path(@RequestParam int src, @RequestParam int trgt) throws Exception{
        if(!osmGraph.isGraphLoaded()) throw new Exception();
        return dijkstra.getPath(src, trgt);
    }
}
