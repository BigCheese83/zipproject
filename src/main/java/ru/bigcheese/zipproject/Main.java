package ru.bigcheese.zipproject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Application main class
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Error! Need a root directory parameter. Run application again with parameter.");
            System.exit(1);
        }
        String root = args[0];
        if (!Files.exists(Paths.get(root))) {
            System.err.println("Invalid parameter. Directory " + root + " not exists");
            System.exit(1);
        }
        Zipper zipper = new Zipper(root);
        zipper.generateFilesList(new FilesFilter());
        zipper.zip(root + ".zip");
    }
}
