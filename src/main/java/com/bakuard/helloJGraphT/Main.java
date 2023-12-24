package com.bakuard.helloJGraphT;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        FileGraphBuilder fileGraphBuilder = new FileGraphBuilder();
        Map<Path, FileData> result = fileGraphBuilder.buildDirectedGraph(
                "/home/bakuard/java/myExperiments/hello-jgrapht/testData/simpleTest");
        result.keySet().forEach(key -> System.out.println(key));
    }
}