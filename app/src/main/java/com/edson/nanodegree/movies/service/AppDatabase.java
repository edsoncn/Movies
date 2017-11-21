package com.edson.nanodegree.movies.service;

import android.app.Activity;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.edson.nanodegree.movies.bean.MovieBean;
import com.edson.nanodegree.movies.dao.MovieDao;

/**
 * Created by edson on 18/11/2017.
 */
@Database(entities = {MovieBean.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static String MOVIES_DB_NAME = "MoviesDB";
    private static AppDatabase moviesDBSingleton = null;

    public abstract MovieDao movieDao();

    public static AppDatabase getMoviesDBSingleton(Context context){
        if(moviesDBSingleton == null) {
            moviesDBSingleton = Room.databaseBuilder(context,
                    AppDatabase.class, MOVIES_DB_NAME).build();
        }
        return moviesDBSingleton;
    }

}
