package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class DummyActivity extends AppCompatActivity {

    ImageView imgDummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        imgDummy = (ImageView) findViewById(R.id.imgDummy);

        if (getIntent() != null) {
            String front = getIntent().getStringExtra("front");

            File file = new File(front);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imgDummy.setImageBitmap(bitmap);
            }
        }
    }
}
