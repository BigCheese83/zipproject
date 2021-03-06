# ZipProject

Утилита для архивирования в формате zip.
Основная особенность - управление процессом архивирования с помощью конфигурационного файла .zipignore.
В этом файле можно задать шаблон файлов или директорий, которые не будут включаться в архив.
Данная утилита помогает быстро сформировать архив с исходниками без вспомогательных файлов, например, файлов, генерируемых IDE, системами автоматической сборки, и пр.

## Системные требования
- JRE 1.8

## Настройка

Предварительно, в папке с программой создать файл .zipignore.
Если этого не сделать, будут архивированы все файлы.
Указать шаблоны файлов/папок, который будут игнорированы при архивировании.
Поддерживаются следующие шаблоны:
* `#` - комментарий
* `{hidden}` - исключаются все скрытые файлы
* `{file}` - исключаются все файлы
* `{dir}` - исключаются все папки
* `[папка]/` - исключается папка и все находящиеся в ней файлы/папки
* `маска *` - любой символ или его отсутствие
