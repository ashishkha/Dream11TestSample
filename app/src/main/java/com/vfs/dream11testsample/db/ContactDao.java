package com.vfs.dream11testsample.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contactentitymodel")
    List<ContactEntityModel> getAll();

    @Insert
    void insert(ContactEntityModel contact);

    @Delete
    void delete(ContactEntityModel contact);

    @Update
    void update(ContactEntityModel contact);
}
