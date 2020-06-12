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

public class ResizeActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1 ;
    Bitmap yourBitmap ;
    private ImageView previewImg;
    private TextView gallery;
    private TextView listSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resize);

        previewImg = (ImageView) findViewById(R.id.previewImg);
        gallery = (TextView) findViewById(R.id.gallery);
        listSize = (TextView) findViewById(R.id.listSize);
        listSize.setVisibility(View.GONE);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent. ACTION_GET_CONTENT ) ;
                intent.setType( "image/*" ) ;
                startActivityForResult(intent , PICK_IMAGE ) ;
            }
        });
    }

    @Override
    public void onActivityResult ( int requestCode , int resultCode , Intent data) {
        super .onActivityResult(requestCode , resultCode , data) ;
        if (requestCode == PICK_IMAGE && resultCode == Activity. RESULT_OK ) {
            if (data == null ) {
                //Display an error
                listSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ResizeActivity.this, "Select Image first", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            try {
                listSize.setVisibility(View.VISIBLE);
                Uri imageUri = data.getData() ;
                yourBitmap = MediaStore.Images.Media. getBitmap ( this .getContentResolver() , imageUri) ;
                previewImg .setImageBitmap( yourBitmap ) ;

                listSize.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListView listView = new ListView(ResizeActivity.this);
                        List<String> options = new ArrayList<>();
                        options.add("25*35mm");
                        options.add("35*45mm");
                        options.add("40*50mm");
                        options.add("35*49mm");
                        options.add("40*55mm");
                        options.add("51*51mm");

                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ResizeActivity.this, android.R.layout.simple_list_item_1, options);
                        listView.setAdapter(adapter);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ResizeActivity.this);
                        builder.setTitle("Select Option");
                        builder.setCancelable(true);
                        builder.setView(listView);

                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (adapter.getItem(position).equals("25*35mm")) {
                                    Bitmap resized = Bitmap. createScaledBitmap ( yourBitmap , 93 , 130 , true ) ;
                                    previewImg .setImageBitmap(resized) ;
                                }
                                if (adapter.getItem(position).equals("35*45mm")) {
                                    Bitmap resized = Bitmap. createScaledBitmap ( yourBitmap , 130 , 168 , true ) ;
                                    previewImg .setImageBitmap(resized) ;
                                }
                                if (adapter.getItem(position).equals("40*50mm")) {
                                    Bitmap resized = Bitmap. createScaledBitmap ( yourBitmap , 149 , 187 , true ) ;
                                    previewImg .setImageBitmap(resized) ;
                                }
                                if (adapter.getItem(position).equals("35*49mm")) {
                                    Bitmap resized = Bitmap. createScaledBitmap ( yourBitmap , 130 , 183 , true ) ;
                                    previewImg .setImageBitmap(resized) ;
                                }
                                if (adapter.getItem(position).equals("40*55mm")) {
                                    Bitmap resized = Bitmap. createScaledBitmap ( yourBitmap , 149 , 205 , true ) ;
                                    previewImg .setImageBitmap(resized) ;
                                }
                                if (adapter.getItem(position).equals("51*51mm")) {
                                    Bitmap resized = Bitmap. createScaledBitmap ( yourBitmap , 190 , 190 , true ) ;
                                    previewImg .setImageBitmap(resized) ;
                                }

                                dialog.dismiss();
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace() ;
            }
        }
    }
}
