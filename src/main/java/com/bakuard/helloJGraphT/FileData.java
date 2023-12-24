package com.bakuard.helloJGraphT;

import java.nio.file.Path;
import java.util.List;

public record FileData(Path absolutePath, List<Path> dependencies, String data) {

    public boolean containsDependency(FileData file) {
        return dependencies.contains(file.absolutePath());
    }

}
