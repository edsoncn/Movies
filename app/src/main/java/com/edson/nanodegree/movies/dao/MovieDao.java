package com.edson.nanodegree.movies.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.edson.nanodegree.movies.bean.MovieBean;

import java.util.List;

/**
 * Created by edson on 18/11/2017.
 */

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    List<MovieBean> getAll();

    @Insert
    void insertAll(MovieBean... movieBean);

    @Delete
    void delete(MovieBean movieBean);

    @Query("SELECT * FROM movie WHERE id = :id")
    MovieBean findById(int id);
}
