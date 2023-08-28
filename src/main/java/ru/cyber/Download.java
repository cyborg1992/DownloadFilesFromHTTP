package ru.cyber;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.List;

public class Download extends Thread {
    private static final Logger log = Logger.getLogger(Download.class);
    String url;
    List<String> outFiles;
    private final String destinationPath;

    public Download(String url, List<String> outFiles, File outputFolder) {
        this.url = url;
        this.outFiles = outFiles;
        this.destinationPath = outputFolder + "/";
    }

    @Override
    public void run() {
        String logStr = String.format("Начало загрузки: ID потока = %d. Загружается файл: %s",
                Thread.currentThread().getId(), outFiles.get(0));
        System.out.println(logStr);
        log.info(logStr);


        File folder = new File(destinationPath);
        if (!folder.exists()) {
            folder.mkdirs();
            log.info("Путь назначения не найден. Создание");
        }

        long startDownloadFile = System.nanoTime();

        try {
            URL urlConnect = new URL(url);
            byte[] buffer = new byte[1024];
            int count;

            var bis = new BufferedInputStream(urlConnect.openStream());
            var fos = new FileOutputStream(destinationPath + outFiles.get(0));
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            bis.close();
            for (int i = 1; i < outFiles.size(); i++) {
                File sourceFile = new File(destinationPath, outFiles.get(0));
                File destinationFile = new File(destinationPath, outFiles.get(i));
                Files.copy(sourceFile.toPath(), destinationFile.toPath());
            }
        } catch (FileNotFoundException e) {
            logStr = String.format("%s: ссылка не действительна", url);
            System.out.println(logStr);
            log.error(logStr);
        } catch (FileAlreadyExistsException e) {
            logStr = String.format("%s: такой файл уже существует", e.getMessage());
            System.out.println(logStr);
            log.error(logStr);
        } catch (IOException e) {
            log.error("StackTrace", e);
        }

        StringBuilder sb = new StringBuilder("Завершено: ID потока = " + Thread.currentThread().getId() + ". ");

        long timeDownloadFile = (System.nanoTime() - startDownloadFile) / 1_000_000; //нс -> мс

        double size;
        File file = new File(destinationPath + outFiles.get(0));
        if (file.exists()) {
            size = (double) ((file.length() / 1024) * outFiles.size());
            if (size < 1024) {
                sb.append("Размер файла ")
                        .append(size)
                        .append(" КБ. Файл ")
                        .append(outFiles.get(0))
                        .append(" загружен: ")
                        .append(size)
                        .append(" КБ за ")
                        .append((double) timeDownloadFile / 1000)
                        .append(" с");
            } else {
                sb.append("Размер файла ")
                        .append(size / 1024)
                        .append(" МБ. Файл ")
                        .append(outFiles.get(0))
                        .append(" загружен: ")
                        .append(size / 1024)
                        .append(" MB за ")
                        .append((double) timeDownloadFile / 1000)
                        .append(" с");
            }
            log.info(sb.toString());
            System.out.println(sb);
            FileInfo.addSize(size);
        }
    }
}
