package com.example.scanner;

import android.graphics.Bitmap;

public class BitmapHelper {
    private Bitmap bitmap = null;
    private Bitmap twoBitmap = null;
    private static final BitmapHelper instance = new BitmapHelper();

    public BitmapHelper() {
    }

    public static BitmapHelper getInstance() {
        return instance;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getTwoBitmap() {
        return twoBitmap;
    }

    public void setTwoBitmap(Bitmap twoBitmap) {
        this.twoBitmap = twoBitmap;
    }
}
