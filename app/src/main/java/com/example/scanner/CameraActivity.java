package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private ApplicationPreference applicationPreference;

    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView captureBtn;
    private ImageView flashOn;
    boolean flash = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        applicationPreference = SpInstance.getInstance(CameraActivity.this);

        final Camera.Parameters params = mCamera.getParameters();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        flashOn = (ImageView) findViewById(R.id.flashOn);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the focus mode
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                // set Camera parameters
                mCamera.setParameters(params);
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

        captureBtn = (ImageView) findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    /** Check if this device has a camera */
    @SuppressLint("UnsupportedChromeOsCameraSystemFeature")
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            BitmapHelper.getInstance().setBitmap(bitmap);

            if (BitmapHelper.getInstance().getBitmap() == null) {
                Toast.makeText(CameraActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(CameraActivity.this, PreviewActivity.class);
                startActivity(intent);
            }
        }
    };
}
