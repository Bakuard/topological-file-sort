package com.bakuard.helloJGraphT;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        FileGraph fileGraph = new FileGraph();
        Map<Path, FileData> graph = fileGraph.buildDirectedGraph(
                "/home/bakuard/java/myExperiments/hello-jgrapht/testData/simpleGraph");
        List<FileData> sortedResult = fileGraph.topologicalSort(graph);
        sortedResult.forEach(f -> System.out.println(f));
    }
}