package com.edson.nanodegree.movies.app;

import android.os.AsyncTask;
import android.util.Log;

import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MoviesListBean;
import com.edson.nanodegree.movies.factory.MoviesListFactory;
import com.edson.nanodegree.movies.helper.AppDatabase;
import com.edson.nanodegree.movies.service.LoadMoviesFavoriteService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edson on 21/11/2017.
 */

public class MoviesFavoritesFragment extends AbstractMoviesListFragment {

    private static final String LOG_TAG = MoviesFavoritesFragment.class.getSimpleName();

    protected LoadMoviesFavoriteService loadMovies;
    protected AppDatabase moviesDB;
    protected boolean passByInit;
    protected int totalResults;

    @Override
    protected MoviesListBean createMoviesListBean() {
        return MoviesListFactory.createMoviesListFavorites(getContext());
    }

    @Override
    public void init() {
        super.init();

        moviesDB = AppDatabase.getMoviesDBSingleton(getContext());
        loadMovies = new LoadMoviesFavoriteService(getContext());
        moviesListBean.setLoadMovies(loadMovies);

        passByInit = true;
        totalResults = -1;
        setTotalResults();
    }

    @Override
    public void onResume() {
        Log.i(LOG_TAG, "onResume");

        if(!passByInit){
            validateQuantityChanged();
        }

        passByInit = false;
        super.onResume();
    }

    public void validateQuantityChanged(){
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return moviesDB.movieDao().count();
                } catch (Exception e) {
                    Log.i(LOG_TAG, "There was an error trying to count favorites: " + e.getMessage());
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer count) {
                if (count >= 0) {
                    if(count != totalResults){
                        passByInit = true;
                        reset();
                    }
                    totalResults = count;
                }
            }
        }.execute();
    }

    public void setTotalResults(){
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return moviesDB.movieDao().count();
                } catch (Exception e) {
                    Log.i(LOG_TAG, "There was an error trying to count favorites: " + e.getMessage());
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer count) {
                totalResults = count;
            }
        }.execute();
    }

}
