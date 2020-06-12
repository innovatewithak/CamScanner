package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfActivity extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        pdfView = (PDFView) findViewById(R.id.pdfView);

        if (getIntent() != null) {
            String path = getIntent().getStringExtra("path");
            Log.d("TAG", "pdfPathVIew: " + path);
            File file = new File(path);;
//            pdfView.fromUri(uri);
        }
    }
}
