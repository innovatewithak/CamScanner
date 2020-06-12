package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scanner.roomdb.Document;
import com.example.scanner.roomdb.DocumentViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.scanner.utils.Constants.BaseDir.PDFDIR;

public class FullviewActivity extends AppCompatActivity {

    private DocumentViewModel viewModel;

    private TextView createPdf;
    private TextView shareBtn;
    private TextView deleteBtn;
    private ImageView fullPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullview);

        createPdf = (TextView) findViewById(R.id.createPdf);
        shareBtn = (TextView) findViewById(R.id.shareBtn);
        deleteBtn = (TextView) findViewById(R.id.deleteBtn);
        fullPreview = (ImageView) findViewById(R.id.fullPreview);

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);

        if (getIntent() != null) {
            final int documentId = getIntent().getIntExtra("documentId", 0);
            final String path = getIntent().getStringExtra("path");
            Log.d("TAG", "pathImg: " + path);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "documentId: " + documentId);
                    viewModel.deleteById(documentId);
                    finish();
                }
            });

            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                fullPreview.setImageBitmap(bitmap);
            }

//            final Bitmap bitmap = BitmapHelper.getInstance().getBitmap();

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        File file = new File(path);
//                        FileOutputStream fOut = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//                        fOut.flush();
//                        fOut.close();
                        file.setReadable(true, false);
                        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID +".provider", file);

                        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setType("image/png");

                        startActivity(Intent.createChooser(intent, "Share image via"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            createPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            //                    String
                            long current = System.currentTimeMillis();
                            String fileName = current + ".pdf";
                            final String filePath = PDFDIR + fileName;

//                        File pictureFile = new File(filePath);

                            File fileImg = new File(path);
                            if (fileImg.exists()) {
                                Bitmap bm = BitmapFactory.decodeFile(fileImg.getAbsolutePath());
                                PdfDocument pdfDocument = new PdfDocument();
                                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bm.getWidth(), bm.getHeight(), 1).create();

                                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                                Canvas canvas = page.getCanvas();
                                Paint paint = new Paint();
                                paint.setColor(Color.parseColor("#FFFFFF"));
                                canvas.drawPaint(paint);

                                bm = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
                                paint.setColor(Color.BLUE);
                                canvas.drawBitmap(bm, 0, 0, null);

                                pdfDocument.finishPage(page);

                                File file = new File(filePath);
                                File dir = new File(PDFDIR);
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                pdfDocument.writeTo(fileOutputStream);

                                pdfDocument.close();

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
                                final String timestamp = simpleDateFormat.format(new Date());

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                final String date = dateFormat.format(new Date());

                                Document document = new Document();
                                document.setName(date);
                                document.setDate(date);
                                document.setCategory("3");
                                document.setPath(filePath);
                                document.setPageCount(1);
                                document.setScanned(timestamp);
                                viewModel.saveDocument(document);

                                Intent intent = new Intent(FullviewActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Toast.makeText(FullviewActivity.this, "No memory card detected", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
