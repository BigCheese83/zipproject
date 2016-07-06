package ru.bigcheese.zipproject;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ZipFilteredFileVisitor extends SimpleFileVisitor<Path> {

    private final String rootPath;
    private final String separator;
    private final FilesFilter filter;
    private final ZipInfo zipInfo;

    public ZipFilteredFileVisitor(String rootPath, String separator, FilesFilter filter, ZipInfo zipInfo) {
        this.rootPath = rootPath;
        this.separator = separator;
        this.filter = filter;
        this.zipInfo = zipInfo;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (rootPath.equals(dir.toString())) {
            return FileVisitResult.CONTINUE;
        }
        if (filter.accept(dir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                // Если каталог пустой, включаем в список
                if (!stream.iterator().hasNext()) {
                    zipInfo.getFiles().add(getRelativePath(dir.toString()) + separator);
                }
            }
            zipInfo.incrementDirCounter();
            return FileVisitResult.CONTINUE;
        }
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (filter.accept(file)) {
            zipInfo.getFiles().add(getRelativePath(file.toString()));
            zipInfo.incrementFileCounter();
        }
        return FileVisitResult.CONTINUE;
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
