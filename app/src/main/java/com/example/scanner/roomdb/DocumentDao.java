package com.example.scanner.roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Document> documents);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Document document);

    @Query("SELECT * FROM Document ORDER BY documentId DESC")
    LiveData<List<Document>> findAll();

    @Query("SELECT * FROM Document WHERE name like :text ORDER BY documentId DESC")
    LiveData<List<Document>> search(String text);

    @Query("SELECT * FROM Document WHERE category like '%' || :cat || '%' AND name like '%' || :text || '%' GROUP BY category, date ORDER BY documentId DESC")
    LiveData<List<Document>> documentGroup(String cat, String text);

    @Query("SELECT COUNT(documentId) FROM Document WHERE date=:text AND category=:cat")
    int documentCount(String text, String cat);

    @Query("SELECT * FROM Document WHERE date =:text AND category=:cat ORDER BY documentId DESC")
    LiveData<List<Document>> documentByDate(String text, String cat);

    @Query("SELECT * FROM Document GROUP BY category, date ORDER BY name ASC")
    LiveData<List<Document>> documentByName();

    @Query("SELECT * FROM Document GROUP BY category, date ORDER BY scanned DESC")
    LiveData<List<Document>> documentByDate();

    @Update
    void update( Document document );

    @Delete
    void delete( Document document );

    @Query("DELETE FROM Document WHERE documentId=:text")
    void deleteById(int text);
}
