package com.bakuard.helloJGraphT;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        FileFinder fileFinder = new FileFinder();
        List<FileData> result = fileFinder.findAllFilesRecursively(
                "/home/bakuard/java/myExperiments/hello-jgrapht/testData/simpleTest");
        result.forEach(System.out::println);
    }
}