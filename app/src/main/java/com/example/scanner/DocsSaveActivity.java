package com.example.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.effect.EffectFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanner.filters.FilterListener;
import com.example.scanner.filters.FilterViewAdapter;
import com.example.scanner.roomdb.Document;
import com.example.scanner.roomdb.DocumentViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ja.burhanrashid52.photoeditor.CustomEffect;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;

import static com.example.scanner.utils.Constants.BaseDir.CARDDIR;
import static com.example.scanner.utils.Constants.BaseDir.PHOTODIR;

public class DocsSaveActivity extends AppCompatActivity implements FilterListener {

    private PhotoEditorView mPhotoEditorView;
    private RecyclerView mRvTools, mRvFilters;
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private boolean mIsFilterVisible;
    private PhotoEditor mPhotoEditor;
    private ConstraintSet mConstraintSet = new ConstraintSet();

    private DocumentViewModel viewModel;

    private TextView saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs_save);

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);

        showFilter(true);

        saveBtn = (TextView) findViewById(R.id.saveBtn);
        mPhotoEditorView = (PhotoEditorView) findViewById(R.id.croppedImage);
        mPhotoEditorView.getSource().setImageBitmap(BitmapHelper.getInstance().getBitmap());

//        PhotoEditor mPhotoEditor = new PhotoEditor.Builder(this, croppedImage)
//                .setPinchTextScalable(true)
//                .build();
//
//        CustomEffect customEffect = new CustomEffect.Builder(EffectFactory.EFFECT_BRIGHTNESS)
//                .setParameter("brightness", 0.5f)
//                .build();
//
//        mPhotoEditor.setFilterEffect(customEffect);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                File file = saveBitMap(DocsSaveActivity.this, mPhotoEditorView);    //which view you want to pass that view as parameter
//                if (file != null) {
//                    Log.i("TAG", "Drawing saved to the gallery!");
//                } else {
//                    Log.i("TAG", "Oops! Image could not be saved.");
//                }

                try {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        //                    String
                        long current = System.currentTimeMillis();
                        String fileName = current + ".png";
                        final String filePath = PHOTODIR + fileName;

//                        File pictureFile = new File(filePath);

                        File file = new File(filePath);
                        File dir = new File(PHOTODIR);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        if (!file.exists()) {
                            file.createNewFile();
                        }

//                        pictureFile.createNewFile();
                        SaveSettings saveSettings = new SaveSettings.Builder()
                                .setClearViewsEnabled(true)
                                .setTransparencyEnabled(true)
                                .build();

                        mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                            @Override
                            public void onSuccess(@NonNull String imagePath) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
                                final String timestamp = simpleDateFormat.format(new Date());

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                final String date = dateFormat.format(new Date());

                                Document document = new Document();
                                document.setName(date);
                                document.setDate(date);
                                document.setCategory("1");
                                document.setPath(filePath);
                                document.setPageCount(1);
                                document.setScanned(timestamp);
                                viewModel.saveDocument(document);

                                Intent intent = new Intent(DocsSaveActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });
                    } else {
                        Toast.makeText(DocsSaveActivity.this, "No memory card detected", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mRvFilters = (RecyclerView) findViewById(R.id.rvFilterView);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

//        mPhotoEditor.setOnPhotoEditorListener(this);
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
//        mConstraintSet.clone(mRootView);
//
//        ChangeBounds changeBounds = new ChangeBounds();
//        changeBounds.setDuration(350);
//        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
//        TransitionManager.beginDelayedTransition(mRootView, changeBounds);
//
//        mConstraintSet.applyTo(mRootView);
    }


    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(PHOTODIR);
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        long current = System.currentTimeMillis();
        String fileName = current + ".png";
        String filePath = PHOTODIR + fileName;

        File file = new File(filePath);
        File dir = new File(PHOTODIR);


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

            Intent intent = new Intent(DocsSaveActivity.this, MainActivity.class);
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
