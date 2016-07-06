package ru.bigcheese.zipproject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>Сжимает файлы в zip архив.
 * Для сжатия используется <tt>Deflater.BEST_COMPRESSION</tt>.</p>
 *
 * @see     ru.bigcheese.zipproject.FilesFilter
 * @see     ru.bigcheese.zipproject.ZipFilteredFileVisitor
 * @see     ru.bigcheese.zipproject.ZipInfo
 * @author  BigCheese
 * @since   JDK1.8
 */
public class Zipper {

    private static final int BUFFER_SIZE = 1024;

    private final String rootPath;
    private final String separator = FileSystems.getDefault().getSeparator();
    private final ZipInfo zipInfo = new ZipInfo();

    /**
     * Создает экземпляр класса <code>Zipper</code>.
     *
     * @param rootPath путь к корневому каталогу
     */
    public Zipper(final String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Сжимает файлы в zip архив.
     *
     * @param zipFile полный путь к итоговому zip-файлу
     * @throws IOException если возникла ошибка ввода/вывода при сжатии файлов
     */
    public void zip(final String zipFile) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
            out.setLevel(Deflater.BEST_COMPRESSION);
            System.out.println("Zip to file: " + zipFile);

            for (String nextFile : zipInfo.getFiles()) {
                System.out.println("Processing: " + nextFile);
                ZipEntry entry = new ZipEntry(nextFile);
                out.putNextEntry(entry);
                if (!nextFile.endsWith(separator)) {
                    try (FileInputStream in = new FileInputStream(rootPath + separator + nextFile)) {
                        int len;
                        while ((len = in.read(buffer)) >= 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
                out.closeEntry();
            }

            System.out.println("Success! Zipped " + zipInfo.getFileCounter() + " files and "
                    + zipInfo.getDirCounter() + " directories.");
        }
    }

    /**
     * Формирует список файлов/каталогов, включаемых в архив.
     *
     * @param filter фильтр файлов/каталогов
     * @throws IOException если возникла ошибка ввода/вывода
     */
    public void generateFilesList(final FilesFilter filter) throws IOException {
        System.out.println("Generate files list ...");
        Files.walkFileTree(Paths.get(rootPath), new ZipFilteredFileVisitor(rootPath, separator, filter, zipInfo));
    }
}
