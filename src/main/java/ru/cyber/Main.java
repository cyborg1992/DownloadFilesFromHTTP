package ru.cyber;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            print("INFO", "Отсутствуют аргументы при запуске утилиты");
            return;
        }
        print("INFO", "Утилита запущена с аргументами: " + Arrays.toString(args));
        int threadCount = Integer.parseInt(args[0]);
        File outputFolder = new File(args[1]);
        File linksFile = new File(args[2]);

        if (!linksFile.exists() || !linksFile.isFile()) {
            print("ERROR", String.format("Файл '%s' не найден%n", linksFile));
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        FileLinks fileLinks = new FileLinks();
        Map<String, List<String>> mapURL = fileLinks.getMapUrlToFiles(linksFile);

        print("INFO", "В списке для скачивания находятся " + mapURL.size() + " уникальных ссылок.");
        print("INFO", "Доступно процессоров: " + Runtime.getRuntime().availableProcessors());

        long start = System.nanoTime();
        // Перебираем mapURL, передаём ключ со значением в конструктор и запускаем потоки
        mapURL.forEach((url, listFiles) -> executor.submit(new Download(url, listFiles, outputFolder)));
        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            print("ERROR", "StackTrace", e);
        }

        long time = (System.nanoTime() - start) / 1_000_000; //нс -> мс

        FileInfo fileInfo = new FileInfo();
        print("INFO", "Завершено: 100%");
        print("INFO", "Загружено: " + mapURL.size() + " файлов " + fileInfo.getTotalSize() / 1024 + " MB");
        print("INFO", "Время: " + (double) time / 1000 + " секунд");
        print("INFO", "Средняя скорость: " + fileInfo.getTotalSize() * 1024 / time + " kB/s");
    }

    public static void print(String type, String string) {
        System.out.println(string);
        if (type.equals("INFO")) log.info(string);
        if (type.equals("ERROR")) log.error(string);
    }

    public static void print(String type, String string, Throwable e) {
        System.out.println(string);
        if (type.equals("INFO")) log.info(string, e);
        if (type.equals("ERROR")) log.error(string, e);
    }

}