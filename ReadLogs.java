import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ReadLogs {

    public static int countLines(String filePath) {
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            int lines = 0;
            while (file.readLine() != null) {
                lines++;
            }
            return lines;
        } catch (IOException e) {
            return 0;
        }
    }

    public static void parseLine(String filePath, int lineNumber, Map<String, Map<String, Integer>> dailyCounts) {
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 0;

            while ((line = file.readLine()) != null) {
                if (currentLine == lineNumber) {
                    if (line.length() < 23) return;

                    String date = line.substring(1, 11);
                    String level = line.substring(22, line.indexOf(':', 22));

                    dailyCounts.putIfAbsent(date, new HashMap<>());
                    dailyCounts.get(date).put(level, dailyCounts.get(date).getOrDefault(level, 0) + 1);

                    return;
                }
                currentLine++;
            }
        } catch (IOException e) {
            // Ignore file read errors
        }
    }

    public static void processFile(String filePath) {
        int numLines = countLines(filePath);
        Map<String, Map<String, Integer>> dailyCounts = new HashMap<>();

        System.out.println("Summary for file: " + filePath);
        for (int i = 0; i < numLines; ++i) {
            parseLine(filePath, i, dailyCounts);
        }

        for (Map.Entry<String, Map<String, Integer>> entry : dailyCounts.entrySet()) {
            String date = entry.getKey();
            Map<String, Integer> counts = entry.getValue();
            System.out.println("Date: " + date);
            for (Map.Entry<String, Integer> countEntry : counts.entrySet()) {
                String level = countEntry.getKey();
                int count = countEntry.getValue();
                System.out.println("  " + level + ": " + count);
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Map<String, String> files = new TreeMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("logs"), "*.txt")) {
            for (Path entry : stream) {
                files.put(entry.getFileName().toString(), entry.toString());
            }
        } catch (IOException e) {
            // Ignore directory read errors
        }

        for (Map.Entry<String, String> entry : files.entrySet()) {
            processFile(entry.getValue());
        }
    }
}

