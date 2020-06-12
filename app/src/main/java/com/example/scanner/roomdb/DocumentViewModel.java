package com.example.scanner.roomdb;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DocumentViewModel extends AndroidViewModel {

    private DocumentDao dao;
    private ExecutorService executorService;

    public DocumentViewModel(@NonNull Application application) {
        super(application);
        dao = DocumentDatabase.getInstance(application).documentDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Document>> getAllDocuments() {
        return dao.findAll();
    }

    public LiveData<List<Document>> search(String text) {
        return dao.search(text);
    }

    public LiveData<List<Document>> documentGroup(String cat, String text) {
        return dao.documentGroup(cat, text);
    }

    public int documentCount(String text, String cat) {
        return dao.documentCount(text, cat);
    }

    public LiveData<List<Document>> documentByDate(String text, String cat) {
        return dao.documentByDate(text, cat);
    }

    public LiveData<List<Document>> documentByName() {
        return dao.documentByName();
    }

    public LiveData<List<Document>> documentByDate() {
        return dao.documentByDate();
    }

    public void saveDocument(final Document document) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dao.save(document);
            }
        });
    }

    public void updateDocument(final Document document) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dao.update(document);
            }
        });
    }

    public void deleteDocument(final Document document) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(document);
            }
        });
    }

    public void deleteById(final int text) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteById(text);
            }
        });
    }
}
