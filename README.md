#Запуск авто-тестов

##Шаги по воспроизведению :
1. Запустить контейнеры в терминале
`docker-compose up`

2. Запустить SUT командой
`java -jar artifacts/aqa-shop.jar`

3. Приложение должно запуститься на:
`http://localhost:8080/`

4.Запустить авто-тесты командой
`gradlew clean test`
или
`./gradlew clean test`

5.Сгенерировать отчеты
`gradlew allureReport
gradlew allureServe`

6.Для завершения работы allureServe выполнить команду:
`Ctrl + С` 
далее Y

7.Остановить и удалить все контейнеры
`docker-compose down`