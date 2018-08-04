package ru.bigcheese.zipproject;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ZipFilteredFileVisitor extends SimpleFileVisitor<Path> {

    private final String rootPath;
    private final FilesFilter filter;
    private final ZipInfo zipInfo = new ZipInfo();

    public ZipFilteredFileVisitor(String rootPath, FilesFilter filter) {
        this.rootPath = rootPath;
        this.filter = filter;
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
                    zipInfo.getFiles().add(getRelativePath(dir.toString()) + FileSystems.getDefault().getSeparator());
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

    public ZipInfo getZipInfo() {
        return zipInfo;
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
