package de.unistuttgart.Programmierprojekt.Controllers;

import de.unistuttgart.Programmierprojekt.Computing.OSMGraph;
import de.unistuttgart.Programmierprojekt.Models.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphController {
    private OSMGraph osmGraph;

    @Autowired
    public GraphController(OSMGraph osmGraph){
        this.osmGraph = osmGraph;
    }

    @RequestMapping("graphStatus")
    public Boolean graphStatus() throws Exception {
        return osmGraph.isGraphLoaded();
    }
    @RequestMapping("closestNode")
    public Node closestNode(@RequestParam double lat, @RequestParam double lon) throws Exception {
        return osmGraph.getClosestNode(lat, lon);
    }

    @RequestMapping("coordinates")
    public double[][] coordinatesFromId(@RequestParam Integer[] ids){
        return osmGraph.idsToCoordinates(ids);
    }
}