package com.edson.nanodegree.movies.dao;

import androidx.paging.TiledDataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.paging.LimitOffsetDataSource;

import com.edson.nanodegree.movies.bean.MovieBean;

import java.util.List;

/**
 * Created by edson on 18/11/2017.
 */

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    List<MovieBean> getAll();

    @Query("SELECT * FROM movie")
    LimitOffsetDataSource<MovieBean> getAllWithPagination();

    @Query("SELECT count(id) FROM movie")
    Integer count();

    @Insert
    void insertAll(MovieBean... movieBean);

    @Delete
    void delete(MovieBean movieBean);

    @Query("SELECT * FROM movie WHERE id = :id")
    MovieBean findById(int id);

}
