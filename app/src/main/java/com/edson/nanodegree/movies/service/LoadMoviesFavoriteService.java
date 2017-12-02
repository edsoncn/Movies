package com.edson.nanodegree.movies.service;

import android.content.Context;
import android.util.Log;

import com.edson.nanodegree.movies.bean.MovieBean;
import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.helper.AppDatabase;

import java.util.List;

/**
 * Created by edson on 21/11/2017.
 */

public class LoadMoviesFavoriteService extends AbstractLoadMovies{

    private static final String LOG_TAG = LoadMoviesFavoriteService.class.getSimpleName();

    private AppDatabase moviesDB;

    public LoadMoviesFavoriteService(Context context) {
        super(context);
    }

    @Override
    void initParameters(Context context) {
        moviesDB = AppDatabase.getMoviesDBSingleton(context);
    }

    @Override
    public void loadMovies(MoviesGroupBean moviesGroupBean){

        int totalResults = moviesDB.movieDao().count();
        moviesGroupBean.setRemoteTotalResults(totalResults);
        moviesGroupBean.setRemoteTotalPages((totalResults - 1) / moviesGroupBean.getPageSize() + 1);

        int startPosition = moviesGroupBean.getPageSize() * (moviesGroupBean.getRemotePage() - 1);
        int count = moviesGroupBean.getPageSize();
        Log.i(LOG_TAG, "Favorites query: totalResults = " + totalResults + ", startPosition = " + startPosition + ", count = " + count);

        List<MovieBean> listMovies = moviesDB.movieDao().getAllWithPagination().loadRange(startPosition, count);
        for (MovieBean movieBean : listMovies){
            moviesGroupBean.addMovie(movieBean);
        }
    }

}
