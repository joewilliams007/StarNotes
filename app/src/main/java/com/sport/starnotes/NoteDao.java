package com.sport.starnotes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM note ORDER BY last_edited DESC")
    List<Note> getAll();

    @Query("SELECT * FROM note WHERE star = 1 ORDER BY last_edited DESC")
    List<Note> getAllStar();

    @Query("SELECT * FROM note ORDER BY created DESC")
    List<Note> getAllCreated();

    @Query("SELECT * FROM note WHERE uid IN (:noteIds)")
    List<Note> loadAllByIds(int[] noteIds);

    @Query("SELECT * FROM note WHERE name LIKE :name LIMIT 1")
    Note findByName(String name);

    @Query("SELECT * FROM note WHERE uid = :uid LIMIT 1")
    Note getByID(int uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Note... notes);

    @Query("UPDATE note SET last_edited = :last_edited, name = :title, note = :note, star = :star WHERE uid = :uid")
    void updateNote(String title, String note, long last_edited, Boolean star, int uid);

    @Query("DELETE FROM note WHERE uid = :uid")
    void deleteNote(int uid);

    @Delete
    void delete(Note note);

    @Query("SELECT COUNT(note) FROM note")
    int getRowCount();
}
