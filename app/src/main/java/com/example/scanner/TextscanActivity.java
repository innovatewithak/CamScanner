package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextscanActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    private ImageView previewImg;
    private TextView gallery;
    Bitmap yourBitmap ;
    private TextView recogniseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textscan);

        recogniseText = (TextView) findViewById(R.id.recogniseText);
        previewImg = (ImageView) findViewById(R.id.previewImg);
        gallery = (TextView) findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent. ACTION_GET_CONTENT ) ;
                intent.setType( "image/*" ) ;
                startActivityForResult(intent , PICK_IMAGE ) ;
            }
        });

        recogniseText.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult ( int requestCode , int resultCode , Intent data) {
        super .onActivityResult(requestCode , resultCode , data) ;
        if (requestCode == PICK_IMAGE && resultCode == Activity. RESULT_OK ) {
            if (data == null ) {
                //Display an error
                return;
            }
            try {
                recogniseText.setVisibility(View.VISIBLE);
                Uri imageUri = data.getData() ;
                yourBitmap = MediaStore.Images.Media. getBitmap ( this .getContentResolver() , imageUri) ;
                previewImg .setImageBitmap(yourBitmap) ;

                recogniseText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BitmapHelper.getInstance().setBitmap(yourBitmap);

                        if (BitmapHelper.getInstance().getBitmap() == null) {
                            Toast.makeText(TextscanActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(TextscanActivity.this, RecogniseActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace() ;
            }
        }
    }
}
