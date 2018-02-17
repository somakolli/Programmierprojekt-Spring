package de.unistuttgart.Programmierprojekt;

import de.unistuttgart.Programmierprojekt.Computing.Dijkstra;
import de.unistuttgart.Programmierprojekt.Computing.OSMGraph;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@Controller
public class ProgrammierprojektApplication {
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ProgrammierprojektApplication.class, args);
		context.getBean(OSMGraph.class).loadFromFile("src/main/resources/germany.fmi");
		context.getBean(Dijkstra.class).init(context.getBean(OSMGraph.class));
	}
}
