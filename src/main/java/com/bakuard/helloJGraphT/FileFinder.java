package com.bakuard.helloJGraphT;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class FileFinder {

    public FileFinder() {

    }

    public List<FileData> findAllFilesRecursively(String rootFolderAbsolutePath) throws IOException {
        List<FileData> result = new ArrayList<>();

        ArrayDeque<Path> queue = new ArrayDeque<>();
        queue.addLast(Paths.get(rootFolderAbsolutePath));

        while(!queue.isEmpty()) {
            Path current = queue.removeFirst();
            if(current.toFile().isDirectory()) putAllFromDirectory(current, queue);
            else parseFile(current, result);
        }

        return result;
    }

    private void putAllFromDirectory(Path path, ArrayDeque<Path> queue) throws IOException {
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            directoryStream.forEach(queue::addLast);
        }
    }

    private void parseFile(Path path, List<FileData> result) throws IOException {
        String content = Files.readString(path);

        FileData fileData = new FileData(
                path,
                new ArrayList<>(),
                content
        );
        result.add(fileData);
    }
}
