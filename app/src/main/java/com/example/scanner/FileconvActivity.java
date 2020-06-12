package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.scanner.roomdb.Document;
import com.example.scanner.roomdb.DocumentViewModel;

import java.util.List;

public class FileconvActivity extends AppCompatActivity {

    private RecyclerView convertedRecy;
    private DocumentViewModel viewModel;
    DocsAdapter adapter = new DocsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileconv);

        convertedRecy = (RecyclerView) findViewById(R.id.convertedRecy);
        convertedRecy.setLayoutManager(new GridLayoutManager(FileconvActivity.this, 2));
        convertedRecy.setHasFixedSize(true);
        convertedRecy.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);
        viewModel.getAllDocuments().observe(this, new Observer<List<Document>>() {
            @Override
            public void onChanged(List<Document> documents) {
                adapter.setDocuments(documents);
            }
        });
    }
}
