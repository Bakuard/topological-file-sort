package com.bakuard.helloJGraphT;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Рекурсивно перебирает все файлы в указанной директории и её под-директориях. Находит в каждом файле ссылку
 * на другой файл. Используя эти данные - создает и возвращает ориентированный граф.
 */
public class FileGraphBuilder {

    private final Pattern linkToOtherFiler;

    public FileGraphBuilder() {
        linkToOtherFiler = Pattern.compile("(require\\s)‘(.*?)’");
    }

    /**
     * Рекурсивно перебирает все файлы в указанной директории и её под-директориях. Находит в каждом файле ссылку
     * на другой файл. Используя эти данные - создает и возвращает ориентированный граф.
     * @param rootFolderAbsolutePath абсолютный путь к директории, с которой начинается поиск файлов
     *                              и построение графа.
     * @return ориентированный граф.
     */
    public Map<Path, FileData> buildDirectedGraph(String rootFolderAbsolutePath) throws IOException {
        Map<Path, FileData> result = new HashMap<>();

        ArrayDeque<Path> queue = new ArrayDeque<>();
        Path root = Paths.get(rootFolderAbsolutePath);
        queue.addLast(root);

        while(!queue.isEmpty()) {
            Path current = queue.removeFirst();
            if(current.toFile().isDirectory()) putAllFromDirectory(current, queue);
            else {
                FileData fileData = parseFile(current, root);
                result.put(fileData.absolutePath(), fileData);
            }
        }

        return result;
    }

    private void putAllFromDirectory(Path path, ArrayDeque<Path> queue) throws IOException {
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            directoryStream.forEach(queue::addLast);
        }
    }

    private FileData parseFile(Path path, Path rootPath) throws IOException {
        String content = Files.readString(path);

        FileData fileData = new FileData(
                path,
                new ArrayList<>(),
                content
        );

        if(!content.isBlank()) {
            Matcher matcher = linkToOtherFiler.matcher(content);
            while (matcher.find()) {
                String rawPath = matcher.group().trim();
                rawPath = rawPath.substring(9, rawPath.length() - 1);
                Path pathToLink = Paths.get(rawPath);
                fileData.dependencies().add(rootPath.resolve(pathToLink));
            }
        }

        return fileData;
    }
}
