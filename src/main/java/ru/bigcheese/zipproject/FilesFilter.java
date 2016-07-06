package ru.bigcheese.zipproject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>Фильтр файлов и каталогов, задаваемый на основе шаблона,
 * который считывается из специального конфигурационного файла <tt>.zipignore</tt>.
 * Поддерживаются следующие шаблоны:
 * <ul>
 *  <li># - комментарий</li>
 *  <li>{hidden} - исключаются все скрытые файлы</li>
 *  <li>{file} - исключаются все файлы</li>
 *  <li>{dir} - исключаются все папки</li>
 *  <li>[папка]/ - исключается папка и все находящиеся в ней файлы/папки</li>
 *  <li> маска * - любой символ или его отсутствие</li>
 * </ul>
 * <p>Если файл <tt>.zipignore</tt> не существует, фильтр будет принимать все файлы.
 *
 * @see     ru.bigcheese.zipproject.Zipper
 * @author  BigCheese
 * @since   JDK1.8
 */
public class FilesFilter {

    private static final String HIDDEN_TEMPLATE = "{hidden}";
    private static final String FILE_TEMPLATE = "{file}";
    private static final String DIR_TEMPLATE = "{dir}";
    private static final String SPECIAL_REGEX_CHARS =
            "[\\\\<\\\\(\\\\[\\\\{\\\\\\\\\\\\^\\\\-\\\\=\\\\$\\\\!\\\\|\\\\]\\\\}\\\\)\\\\?\\\\+\\\\.\\\\>]";
    private static final Pattern MASK_PATTERN = Pattern.compile(".*\\*.*");

    private final Set<String> filterSet = new HashSet<>();
    private final Map<String, Pattern> maskMap = new HashMap<>();

    /**
     * Инициализирует фильтр. Шаблоны фильтра считываются из файла <tt>.zipignore</tt>.
     */
    public FilesFilter() {
        Path ignoreFile = Paths.get(".zipignore");
        if (Files.exists(ignoreFile)) {
            readIgnoreFile(ignoreFile);
        }
    }

    /**
     * Применяет фильтр к заданному файлу.
     * @param file файл
     * @return <tt>true</tt>, если файл прошел фильтр
     */
    public boolean accept(Path file) {
        if (!filterSet.isEmpty()) {
            for (String pattern : filterSet) {
                try {
                    if (match(file, pattern)) {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private void readIgnoreFile(Path file) {
        try {
            Set<String> lines = Files.readAllLines(file)
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                    .collect(Collectors.toSet());
            filterSet.addAll(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean match(Path file, String string) throws IOException {
        if (HIDDEN_TEMPLATE.equals(string)) {
            return Files.isHidden(file);
        }
        if (FILE_TEMPLATE.equals(string)) {
            return Files.isRegularFile(file);
        }
        if (DIR_TEMPLATE.equals(string)) {
            return Files.isDirectory(file);
        }

        String filename = file.toString().toLowerCase();

        if (string.endsWith("/") && Files.isDirectory(file)) {
            return filename.contains(string.substring(0, string.length() - 1));
        }

        if (MASK_PATTERN.matcher(string).matches()) {
            Pattern pattern = maskMap.computeIfAbsent(string, k -> {
                String regex = ".*" + string.replaceAll(SPECIAL_REGEX_CHARS, "\\\\$0").replace("*", ".*");
                return Pattern.compile(regex);
            });
            return pattern.matcher(filename).matches();
        }

        return false;
    }
}
