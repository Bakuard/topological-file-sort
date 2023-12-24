package com.bakuard.helloJGraphT;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Рекурсивно перебирает все файлы в указанной директории и её под-директориях. Находит в каждом файле ссылку
 * на другой файл. Используя эти данные - создает и возвращает ориентированный граф.
 */
public class FileGraph {

    private final Pattern linkToOtherFiler;

    public FileGraph() {
        linkToOtherFiler = Pattern.compile("(require\\s)‘(.*?)’");
    }

    /**
     * Создает ориентированный граф из файлов используя метод {@link #buildDirectedGraph(String)},
     * затем сортирует его используя {@link #topologicalSort(Map)}. Содержимое всех файлов в итоговом
     * упорядоченном списке записывает в один общий файл по указанному пути.
     * @param rootFolderAbsolutePath абсолютный путь к директории, с которой начинается поиск файлов
     *                               и построение графа.
     * @param outputFolderAbsolutePath абсолютный путь к файлу, куда будет записан результат.
     * @throws CycleGraphException если в получившемся графе есть циклы.
     *                             В сообщение будет указан найденный цикл.
     */
    public void concatTopologicalSortedFiles(String rootFolderAbsolutePath,
                                             String outputFolderAbsolutePath) throws IOException {
        Map<Path, FileData> graph = buildDirectedGraph(rootFolderAbsolutePath);
        List<FileData> sortedResult = topologicalSort(graph);

        Path outputPath = Paths.get(outputFolderAbsolutePath);
        for(FileData fileData : sortedResult) {
            String content = Files.readString(fileData.absolutePath());
            Files.writeString(outputPath,
                    content + '\n',
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        }
    }


    /**
     * Выполняет топологическую сортировку для переданного ориентированного графа. Если
     * граф является циклическим - генерирует исключение.
     * @param graph ориентированный граф
     * @throws CycleGraphException если переданные граф содержит циклы.
     *                             В сообщение будет указан найденный цикл.
     */
    public List<FileData> topologicalSort(Map<Path, FileData> graph) {
        List<FileData> result = new ArrayList<>();

        Set<FileData> notVisitedVertex = new HashSet<>(graph.values());
        List<FileData> currentVertexes = new ArrayList<>();
        // Данный цикл нужен, чтобы не потерять изолированные вершины графа.
        while(!notVisitedVertex.isEmpty()) {
            FileData vertex = notVisitedVertex.stream().findAny().orElseThrow();
            topologicalSortUtil(graph, vertex, notVisitedVertex, currentVertexes, result);
        }

        return result;
    }

    private void topologicalSortUtil(Map<Path, FileData> graph,
                                     FileData vertex,
                                     Set<FileData> notVisitedVertex,
                                     List<FileData> currentVertexes,
                                     List<FileData> result) {
        notVisitedVertex.remove(vertex);
        currentVertexes.add(vertex);

        for(Path dependencyPath : vertex.dependencies()) {
            FileData dependency = graph.get(dependencyPath);
            if(currentVertexes.contains(dependency)) {
                String message = findCycle(currentVertexes, dependency)
                        .stream()
                        .map(f -> f.absolutePath().toString())
                        .reduce((accumulator, current) -> accumulator + " ->\n" + current)
                        .orElseThrow();
                throw new CycleGraphException("cycle:\n" + message);
            } else if(notVisitedVertex.contains(dependency)) {
                topologicalSortUtil(graph, dependency, notVisitedVertex, currentVertexes, result);
            }
        }

        currentVertexes.remove(vertex);
        result.add(vertex);
    }

    private List<FileData> findCycle(List<FileData> currentVertexes, FileData alreadyVisitedVertex) {
        List<FileData> cycle = new ArrayList<>();
        cycle.add(alreadyVisitedVertex);

        for(int i = currentVertexes.size() - 1; i >= 0; --i) {
            FileData fileData = currentVertexes.get(i);
            cycle.add(fileData);
            if(fileData.equals(alreadyVisitedVertex)) break;
        }

        return cycle;
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

        FileData fileData = new FileData(path, new ArrayList<>());

        if(!content.isBlank()) {
            Matcher matcher = linkToOtherFiler.matcher(content);
            while(matcher.find()) {
                String rawPath = matcher.group().trim();
                rawPath = rawPath.substring(9, rawPath.length() - 1);
                Path pathToLink = Paths.get(rawPath);
                fileData.dependencies().add(rootPath.resolve(pathToLink));
            }
        }

        return fileData;
    }
}
