package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanner.roomdb.Document;
import com.example.scanner.roomdb.DocumentViewModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.scanner.utils.Constants.BaseDir.CARDDIR;

public class CardMergeActivity extends AppCompatActivity {

    public static final String PICTURE_NAME = "photo_name";

    private LinearLayout mergeLin;
    private TextView saveBtn;
    private TextView shareBtn;
    private ImageView cardFront;
    private ImageView cardBack;

    private DocumentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_merge);

        mergeLin = (LinearLayout) findViewById(R.id.mergeLin);
        saveBtn = (TextView) findViewById(R.id.saveBtn);
        shareBtn = (TextView) findViewById(R.id.shareBtn);
        cardFront = (ImageView) findViewById(R.id.cardFront);
        cardBack = (ImageView) findViewById(R.id.cardBack);

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);

        cardFront.setImageBitmap(BitmapHelper.getInstance().getBitmap());
        cardBack.setImageBitmap(BitmapHelper.getInstance().getTwoBitmap());

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bitmap bitmap = Bitmap.createBitmap(mergeLin.getWidth(), mergeLin.getHeight(), Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bitmap);
//                mergeLin.draw(canvas);
//
//                try {
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
////                    String
//                        long current = System.currentTimeMillis();
//                        String fileName = current + ".png";
//                        String filePath = CARDDIR + fileName;
//
//                        File file = new File(filePath);
//                        File dir = new File(CARDDIR);
//                        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//                        Toast.makeText(CardMergeActivity.this, "Saved", Toast.LENGTH_SHORT).show();
//                        fileOutputStream.flush();
//                        fileOutputStream.close();
//                        if (!dir.exists()) {
//                            dir.mkdirs();
//                        }
//                        if (!file.exists()) {
//                            file.createNewFile();
//                        }
//
////                    Intent intent = new Intent();
////                    intent.putExtra(PICTURE_NAME, fileName);
////                    setResult(1, intent);
////                    finish();
//
//                    } else {
//                        Toast.makeText(CardMergeActivity.this, "No memory card detected", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }


                File file = saveBitMap(CardMergeActivity.this, mergeLin);    //which view you want to pass that view as parameter
                if (file != null) {
                    Log.i("TAG", "Drawing saved to the gallery!");
                } else {
                    Log.i("TAG", "Oops! Image could not be saved.");
                }
            }
        });
    }

    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(CARDDIR);
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        long current = System.currentTimeMillis();
        String fileName = current + ".png";
        String filePath = CARDDIR + fileName;

        File file = new File(filePath);
        File dir = new File(CARDDIR);


//        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
//        String filename = CARDDIR + fileName;
        File pictureFile = new File(filePath);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
            final String timestamp = simpleDateFormat.format( new Date() );

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            final String date = dateFormat.format( new Date() );

            Document document = new Document();
            document.setName(date);
            document.setDate(date);
            document.setCategory("2");
            document.setPath(filePath);
            document.setPageCount(1);
            document.setScanned(timestamp);
            viewModel.saveDocument(document);

            Intent intent = new Intent(CardMergeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
//        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }
    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
}
