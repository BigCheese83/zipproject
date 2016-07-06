package ru.bigcheese.zipproject;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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
 * @see     java.io.FileFilter
 * @see     ru.bigcheese.zipproject.Zipper
 * @author  BigCheese
 * @since   JDK1.7
 */
public class FilesFilter implements FileFilter {

    private final Set<String> filterSet = new HashSet<>();

    /**
     * Инициализирует фильтр. Шаблоны фильтра считываются из файла <tt>.zipignore</tt>.
     */
    public FilesFilter() {
        File ignoreFile = new File(".zipignore");
        if (ignoreFile.exists()) {
            readIgnoreFile(ignoreFile);
        }
    }

    /**
     * Применяет фильтр к заданному файлу.
     * @param file файл
     * @return <tt>true</tt>, если файл прошел фильтр
     */
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
