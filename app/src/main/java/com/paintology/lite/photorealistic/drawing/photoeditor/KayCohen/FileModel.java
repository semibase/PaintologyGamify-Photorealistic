package com.paintology.lite.photorealistic.drawing.photoeditor.KayCohen;

public class FileModel {

    String DirName, FileName;

    public FileModel(String dirName) {
        DirName = dirName;
    }

    public FileModel(String dirName, String fileName) {
        DirName = dirName;
        FileName = fileName;
    }

    public String getDirName() {
        return DirName;
    }

    public void setDirName(String dirName) {
        DirName = dirName;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
}
