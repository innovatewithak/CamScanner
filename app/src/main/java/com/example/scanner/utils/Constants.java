package com.example.scanner.utils;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static class BaseDir {
        public static final String APPNAME = "Scanner";
        public static final String DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APPNAME + File.separator;
        public static final String PHOTODIR = DIR + File.separator + "DOCS" + File.separator;
        public static final String CARDDIR = DIR + File.separator + "CARD" + File.separator;
        public static final String PDFDIR = DIR + File.separator + "PDF" + File.separator;
    }
}
