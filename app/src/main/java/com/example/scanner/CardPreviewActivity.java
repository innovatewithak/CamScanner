package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import vn.nano.photocropper.CropImageView;
import vn.nano.photocropper.CropListener;

public class CardPreviewActivity extends AppCompatActivity {

    private CropImageView previewImage;
    private TextView leftRotate;
    private TextView rightRotate;
    private TextView nextBtn;
    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_preview);

        mBitmap = BitmapHelper.getInstance().getBitmap();
        previewImage = (CropImageView) findViewById(R.id.previewImage);
        leftRotate = (TextView) findViewById(R.id.leftRotate);
        rightRotate = (TextView) findViewById(R.id.rightRotate);
        nextBtn = (TextView) findViewById(R.id.nextBtn);

        if (getIntent() != null) {
            String front = getIntent().getStringExtra("front");

            File file = new File(front);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                previewImage.setImageBitmap(bitmap);
            }
        }

        leftRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                previewImage.setRotation(previewImage.getRotation() - 90);
//                croppedImage.setRotation(croppedImage.getRotation() - 90);
                rotateLeftBitmap();
            }
        });

        rightRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                previewImage.setRotation(previewImage.getRotation() + 90);
//                croppedImage.setRotation(croppedImage.getRotation() + 90);
                rotateBitmap();
            }
        });

        final CropListener listener = new CropListener() {
            @Override
            public void onFinish(Bitmap bitmap) {
                BitmapHelper.getInstance().setBitmap(bitmap);

                if (BitmapHelper.getInstance().getBitmap() == null) {
                    Toast.makeText(CardPreviewActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(CardPreviewActivity.this, CardbackActivity.class);
                    startActivity(intent);
                }
            }
        };

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewImage.crop(listener, true);
            }
        });
    }

    private void rotateBitmap() {
        Matrix matrix = new Matrix();
        matrix.postRotate( 90);

        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
        ((CropImageView) findViewById(R.id.previewImage)).setImageBitmap(mBitmap);
    }

    private void rotateLeftBitmap() {
        Matrix matrix = new Matrix();
        matrix.postRotate( -90);

        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
        ((CropImageView) findViewById(R.id.previewImage)).setImageBitmap(mBitmap);
    }
}
