package com.bakuard.helloJGraphT;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        FileGraph fileGraph = new FileGraph();
        fileGraph.concatTopologicalSortedFiles(
                "/home/bakuard/java/myExperiments/hello-jgrapht/testData/cycleGraph",
                "/home/bakuard/java/myExperiments/hello-jgrapht/testData/output.txt"
        );
    }
}