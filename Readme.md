Tim-Julian Ehret, Julian Blumentröther, Sokol Makolli
Gruppe B

To start the server run "./mvnw spring-boot:run" in the command line.<br>
To change the graph change the graphPath variable in the 
src/main/java/de/unistuttgart/Programmierprojekt/ProgrammierprojektApplication class
and add the graph file preferably to the resources folder(without changes the toy.fmi graph
will be loaded).

The web page will be served to localhost:8080.<br>
After the graph is loaded you can either input the source and the target node 
manually or select them by clicking on the map. When both are selected you can click on
calculate route to calculate and draw the route on the graph.