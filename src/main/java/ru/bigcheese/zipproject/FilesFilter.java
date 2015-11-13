package ru.bigcheese.zipproject;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by BigCheese on 12.11.15.
 */
public class FilesFilter implements FileFilter {

    private final Set<String> filterSet = new HashSet<>();

    public FilesFilter() {
        File ignoreFile = new File(".zipignore");
        if (ignoreFile.exists()) {
            readIgnoreFile(ignoreFile);
        }
    }

    @Override
    public boolean accept(File file) {
        if (filterSet.isEmpty()) return true;
        for (String pattern : filterSet) {
            if (match(file, pattern)) {
                return false;
            }
        }
        return true;
    }

    private void readIgnoreFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    filterSet.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean match(File file, String string) {
        if (string.equals("{hidden}")) {
            return file.isHidden();
        }
        if (string.equals("{file}")) {
            return file.isFile();
        }
        if (string.equals("{dir}")) {
            return file.isDirectory();
        }

        if (string.endsWith("/") && !file.isDirectory()) {
            return false;
        }

        String filename = file.getName().toLowerCase();
        String template = string.replace("/", "").toLowerCase();

        StringTokenizer tokenizer = new StringTokenizer(template, "*", true);
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if ("*".equals(token)) {
                if (tokenizer.hasMoreTokens()) {
                    String next = tokenizer.nextToken();
                    i = filename.indexOf(next, i);
                    if (i == -1) {
                        return false;
                    }
                    i += next.length();
                }
                continue;
            }
            if (!filename.startsWith(token, i)) {
                return false;
            }
            i += token.length();
        }
        return true;
    }
}
