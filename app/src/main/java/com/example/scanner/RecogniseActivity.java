package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class RecogniseActivity extends AppCompatActivity {

    private TextView copyText;
    private EditText textRecog;
    ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognise);

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        copyText = (TextView) findViewById(R.id.copyText);
        textRecog = (EditText) findViewById(R.id.textRecog);

        Bitmap bitmap = BitmapHelper.getInstance().getBitmap();

        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < items.size(); i++) {
                TextBlock myitem = items.valueAt(i);
                sb.append(myitem.getValue());
                sb.append("\n");
            }

            textRecog.setText(sb.toString());

            copyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipData clipData = ClipData.newPlainText("ocrText", textRecog.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(RecogniseActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
