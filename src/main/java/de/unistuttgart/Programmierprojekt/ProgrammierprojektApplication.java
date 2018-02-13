package de.unistuttgart.Programmierprojekt;

import de.unistuttgart.Programmierprojekt.Computing.Dijkstra;
import de.unistuttgart.Programmierprojekt.Computing.OSMGraph;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class ProgrammierprojektApplication {
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(ProgrammierprojektApplication.class, args);
		context.getBean(OSMGraph.class).loadFromFile("src/main/resources/toy.fmi");
		context.getBean(Dijkstra.class).init(context.getBean(OSMGraph.class));
	}
}
