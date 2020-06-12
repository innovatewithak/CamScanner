package com.example.scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.scanner.roomdb.Document;
import com.example.scanner.roomdb.DocumentViewModel;

import java.util.List;

public class DocsActivity extends AppCompatActivity {

    private String date;
    private String cat;
    private RecyclerView docsRecyclerView;
    private DocumentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);

        docsRecyclerView = (RecyclerView) findViewById(R.id.docsRecyclerView);
        docsRecyclerView.setLayoutManager(new GridLayoutManager(DocsActivity.this, 2));
        docsRecyclerView.setHasFixedSize(true);

        if (getIntent() != null) {
            date = getIntent().getStringExtra("date");
            cat = getIntent().getStringExtra("cat");

            final DocsAdapter adapter = new DocsAdapter();
            docsRecyclerView.setAdapter(adapter);

            viewModel = ViewModelProviders.of(this).get(DocumentViewModel.class);
            viewModel.documentByDate(date, cat).observe(this, new Observer<List<Document>>() {
                @Override
                public void onChanged(List<Document> documents) {
                    adapter.setDocuments(documents);
                }
            });
        }
    }
}
