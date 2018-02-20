package de.unistuttgart.Programmierprojekt;

import de.unistuttgart.Programmierprojekt.Computing.Dijkstra;
import de.unistuttgart.Programmierprojekt.Computing.OSMGraph;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

/**
 * @author Tim-Julian Ehret, Julian Blumentröther, Sokol Makolli
 */

@SpringBootApplication
@Controller
public class ProgrammierprojektApplication {
	private static final String graphPath = "src/main/resources/toy.fmi";

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ProgrammierprojektApplication.class, args);
		context.getBean(OSMGraph.class).loadFromFile(graphPath);
		context.getBean(Dijkstra.class).init(context.getBean(OSMGraph.class));
	}
}
