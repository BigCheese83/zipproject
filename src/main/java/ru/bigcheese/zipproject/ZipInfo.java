package ru.bigcheese.zipproject;

import java.util.ArrayList;
import java.util.List;

public class ZipInfo {

    private List<String> files = new ArrayList<>();
    private int fileCounter = 0;
    private int dirCounter = 0;

    public List<String> getFiles() {
        return files;
    }

    public int getFileCounter() {
        return fileCounter;
    }

    public void incrementFileCounter() {
        ++fileCounter;
    }

    public int getDirCounter() {
        return dirCounter;
    }

    public void incrementDirCounter() {
        ++dirCounter;
    }

    @Override
    public String toString() {
        return "ZipInfo{" +
                "files=" + files +
                ", fileCounter=" + fileCounter +
                ", dirCounter=" + dirCounter +
                '}';
    }
}
