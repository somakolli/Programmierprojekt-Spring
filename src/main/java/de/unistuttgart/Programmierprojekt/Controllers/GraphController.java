package de.unistuttgart.Programmierprojekt.Controllers;

import de.unistuttgart.Programmierprojekt.Computing.OSMGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
}