package com.bakuard.helloJGraphT;

import java.nio.file.Path;
import java.util.List;

/**
 * Метаданные файла.
 * @param absolutePath абсолютный путь до файла.
 * @param dependencies список файлов являющихся зависимостью для данного файла.
 */
public record FileData(Path absolutePath, List<Path> dependencies) {

    public boolean containsDependency(FileData file) {
        return dependencies.contains(file.absolutePath());
    }

}
