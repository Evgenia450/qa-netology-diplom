# Дипломный проект по профессии «Тестировщик»

## Запуск авто-тестов

### Перед началом работы
1. Получить доступ к удаленному серверу
2. На удаленном сервере должны быть установлены и доступны:
   - GIT
   - Docker
   - Bash
3. На компьютере пользователя должна быть установлена:
   - Git Bash
   - Intellij IDEA

### Шаги для запуска авто-тестов из консоли
1. Склонировать репозиторий: `git clone https://github.com/Evgenia450/qa-netology-diplom`
4. Сменить папку: `cd qa-netology-diplom`
5. Запустить контейнеры (СУБД MySQL, PostgreSQL, gate-simulator) в консоли: `docker-compose up -d`
6. Запустить SUT
   1. Команда для запуска с поддержкой СУБД MySQL: `java -Dspring.datasource.url=jdbc:mysql://localhost:3306/app -jar artifacts/aqa-shop.jar`
   2. Команда для запуска с поддержкой СУБД PostgreSQL: `java -Dspring.datasource.url=jdbc:postgresql://localhost:5432/app -jar artifacts/aqa-shop.jar`
7. В браузере сервис будет доступен по адресу [http://localhost:8080/](http://localhost:8080/) 
8. Запустить авто-тесты
   1. Команда для запуска тестов с MySQL: `gradlew clean test -Ddb.url=jdbc:mysql://localhost:3306/app`
   2. Команда для запуска тестов с PostgreSQL: `gradlew clean test -Ddb.url=jdbc:postgresql://localhost:5432/app`
9. После завершения прогона авто-тестов сгенерировать отчёт командой `gradlew allureServe`. Отчёт откроется в браузере.
10. Для завершения работы allureServe выполнить команду `Ctrl + С`, далее `Y`
11. Остановить все контейнеры командой `docker-compose down`
