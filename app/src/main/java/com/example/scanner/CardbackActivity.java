package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.scanner.CameraActivity.getCameraInstance;
import static com.example.scanner.utils.Constants.BaseDir.CARDDIR;

public class CardbackActivity extends AppCompatActivity {

    public static final String PICTURE_NAME = "photo_name";

    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView captureBtn;
    private ImageView flashOn;
    boolean flash = false;
    private CameraTopBackRectView topView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardback);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        final Camera.Parameters params = mCamera.getParameters();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        topView = (CameraTopBackRectView) findViewById(R.id.rectOnCamera);
        flashOn = (ImageView) findViewById(R.id.flashOn);
        captureBtn = (ImageView) findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });

        if (!flash) {
            flashOn.setBackground(getResources().getDrawable(R.drawable.ic_flash_off));
        }

        flashOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flash) {
                    flash = true;
                    flashOn.setBackground(getResources().getDrawable(R.drawable.ic_flash_on));
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    mCamera.setParameters(params);
                }
                else {
                    flash = false;
                    flashOn.setBackground(getResources().getDrawable(R.drawable.ic_flash_off));
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(params);
                }
            }
        });
    }

    public Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            topView.draw(new Canvas());
            BufferedOutputStream bos = null;
            Bitmap resultBitmap = null;
            Bitmap rotateBitmap = null;
            Bitmap sizeBitmap = null;
            if (data == null) return;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                resultBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    String
                    long current = System.currentTimeMillis();
                    String fileName = current + ".png";
                    String filePath = CARDDIR + fileName;

                    Matrix m = new Matrix();
                    int height = resultBitmap.getHeight();
                    int width = resultBitmap.getWidth();
                    m.setRotate(90);
                    rotateBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, width, height, m, true);
                    File file = new File(filePath);
                    File dir = new File(CARDDIR);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    sizeBitmap = Bitmap.createScaledBitmap(rotateBitmap,
                            topView.getViewWidth(), topView.getViewHeight(), true);
                    resultBitmap = Bitmap.createBitmap(sizeBitmap, topView.getRectLeft(),
                            topView.getRectTop(),
                            topView.getRectRight() - topView.getRectLeft(),
                            topView.getRectBottom() - topView.getRectTop());
                    resultBitmap = adjustPhotoRotation(resultBitmap, -90);

                    resultBitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos);

//                    Intent intent = new Intent();
//                    intent.putExtra(PICTURE_NAME, fileName);
//                    setResult(1, intent);
//                    finish();

                    Intent intent = new Intent(CardbackActivity.this, BackPreviewActivity.class);
                    intent.putExtra("back", filePath);
                    startActivity(intent);

                } else {
                    Toast.makeText(CardbackActivity.this, "No memory card detected", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.flush();
                        bos.close();
                    }
                    recycleBitmap(rotateBitmap);
                    recycleBitmap(resultBitmap);
                    recycleBitmap(sizeBitmap);
                    camera.stopPreview();
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    public void recycleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) return;
        bitmap.recycle();
        bitmap = null;
    }
}
