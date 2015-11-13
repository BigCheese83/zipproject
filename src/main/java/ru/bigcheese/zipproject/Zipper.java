package ru.bigcheese.zipproject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by BigCheese on 12.11.15.
 */
public class Zipper {

    private static final int BUFFER_SIZE = 1024;
    private final String rootPath;
    private final List<String> files = new ArrayList<>();
    private int fileCounter = 0;
    private int dirCounter = 0;

    public Zipper(String rootPath, FileFilter filter) {
        this.rootPath = rootPath;
        generateFilesList(new File(this.rootPath), filter);
    }

    /**
     * Сжать файлы в zip архив
     */
    public void zip(final String zipFile) {
        byte[] buffer = new byte[BUFFER_SIZE];
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
            out.setLevel(Deflater.BEST_COMPRESSION);
            System.out.println("Zip to file: " + zipFile);
            for (String nextFile : files) {
                System.out.println("Processing: " + nextFile);
                ZipEntry entry = new ZipEntry(nextFile);
                out.putNextEntry(entry);
                if ((nextFile.substring(nextFile.length() - 1)).equals(File.separator)) {
                    continue;
                }
                try (FileInputStream in =
                             new FileInputStream(rootPath + File.separator + nextFile)) {
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            }
            out.closeEntry();
            System.out.println("Success! Zipped " + fileCounter + " files and " + dirCounter + " directories.");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Fatal error!", ex);
        }
    }

    /**
     * Сформировать список всех файлов в каталоге (рекурсивно)
     *
     * @param file root path
     * @param filter file accept
     */
    private void generateFilesList(final File file, final FileFilter filter) {
        if (file.isFile() && filter.accept(file)) {
            files.add(getRelativePath(file.getAbsolutePath()));
            fileCounter++;
        }
        if (file.isDirectory()) {
            String dir = file.getAbsoluteFile().toString();
            if (dir.equalsIgnoreCase(rootPath)) {
                for (String nextFile : file.list()) {
                    generateFilesList(new File(file, nextFile), filter);
                }
            } else if (filter.accept(file)) {
                files.add(dir.substring(rootPath.length() + 1) + File.separator);
                dirCounter++;
                for (String nextFile : file.list()) {
                    generateFilesList(new File(file, nextFile), filter);
                }
            }
        }
    }

    /**
     * Получение относительного пути файла в архиве.
     *
     * @param filename absolute pathname string
     * @return относительный путь файла в архиве
     */
    private String getRelativePath(final String filename) {
        return filename.substring(rootPath.length() + 1);
    }
}
