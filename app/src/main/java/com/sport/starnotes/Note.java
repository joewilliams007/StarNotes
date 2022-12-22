package com.sport.starnotes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Note {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "created")
    public long created;

    @ColumnInfo(name = "last_edited")
    public long last_edited;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "synced")
    public Boolean synced;

    @ColumnInfo(name = "star")
    public Boolean star;
}