package ru.bigcheese.zipproject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>Сжимает файлы в zip архив.
 * Для сжатия используется <tt>Deflater.BEST_COMPRESSION</tt>.</p>
 *
 * @see     ru.bigcheese.zipproject.FilesFilter
 * @author  BigCheese
 * @since   JDK1.7
 */
public class Zipper {

    private static final int BUFFER_SIZE = 1024;
    private final String rootPath;
    private final List<String> files = new ArrayList<>();
    private int fileCounter = 0;
    private int dirCounter = 0;

    /**
     * Создает экземпляр класса <code>Zipper</code>.
     * Формирует список файлов/каталогов, включаемых в архив.
     *
     * @param rootPath путь к корневому каталогу
     * @param filter фильтр файлов/каталогов, включаемых в архив
     */
    public Zipper(String rootPath, FileFilter filter) {
        this.rootPath = rootPath;
        generateFilesList(new File(this.rootPath), filter);
    }

    /**
     * Сжимает файлы в zip архив.
     *
     * @param zipFile полный путь к итоговому zip-файлу
     * @throws RuntimeException если возникла ошибка ввода/вывода при сжатии файлов
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
     * Формирует список всех файлов в каталоге (рекурсивно).
     *
     * @param file корневой каталог
     * @param filter фильтр файлов/каталогов
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
     * @param filename Абсолютный путь к файлу
     * @return относительный путь файла в архиве
     */
    private String getRelativePath(final String filename) {
        return filename.substring(rootPath.length() + 1);
    }
}
