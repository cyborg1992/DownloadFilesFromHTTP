package ru.cyber;

public class FileInfo {
    private static double totalSize = 0;

    public double getTotalSize() {
        return totalSize;
    }

    public static void addSize(double size) {
        totalSize += size;
    }

}
