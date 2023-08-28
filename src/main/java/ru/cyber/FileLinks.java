package ru.cyber;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileLinks {
    private static final Logger log = Logger.getLogger(FileLinks.class);
    private final Map<String, List<String>> mapUrlToFiles = new HashMap<>();

    public Map<String, List<String>> getMapUrlToFiles(File textFile) {
        mapping(getListUrlFromFile(textFile));
        return mapUrlToFiles;
    }

    private List<String> getListUrlFromFile(File textFile) {
        List<String> list = new ArrayList<>();
        try {
            FileReader fr = new FileReader(textFile);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            log.error("StackTrace", e);
        }
        return list;
    }

    private void mapping(List<String> list) {
        for (String s : list) {
            String url = s.split(" ")[0];
            String pathFile = s.split(" ")[1];
            if (!mapUrlToFiles.containsKey(url)) {
                mapUrlToFiles.put(url, new ArrayList<>());
            }
            mapUrlToFiles.get(url).add(pathFile);
        }
    }

}