package com.edson.nanodegree.movies.service;

import android.os.AsyncTask;
import android.util.Log;

import com.edson.nanodegree.movies.bean.MoviesListBean;
import com.edson.nanodegree.movies.bean.MoviesGroupBean;

/**
 * Created by edson on 12/10/2017.
 */

public class MoviesGroupClientRestTask extends AsyncTask<MoviesGroupBean, Void, MoviesGroupBean> {

    private final String LOG_TAG = MoviesGroupClientRestTask.class.getSimpleName();

    private MoviesListBean moviesListBean;

    public MoviesGroupClientRestTask(MoviesListBean moviesListBean){
        this.moviesListBean = moviesListBean;
    }

    @Override
    protected MoviesGroupBean doInBackground(MoviesGroupBean... params) {

        MoviesGroupBean moviesGroupBean = params[0];
        Log.i(LOG_TAG, "Load: " + moviesGroupBean.getTitle());

        try {
            moviesListBean.getLoadMovies().loadMovies(moviesGroupBean);
            return moviesGroupBean;
        }catch (Exception e){
            return null;
        }

    }

    @Override
    protected void onPostExecute(MoviesGroupBean moviesGroupBean) {
        if(moviesGroupBean != null) {
            Log.i(LOG_TAG, "Movies list size: " + moviesGroupBean.getMovies().size());
            Log.i(LOG_TAG, "Movies list temp size: " + moviesGroupBean.getMoviesTemp().size());
            Log.i(LOG_TAG, "Real and current: " + moviesGroupBean.getRealPage() + ", " + moviesGroupBean.getCurrentPage());

            if (moviesGroupBean.validateLoadMoviesPageComplete()) {
                moviesListBean.loadMoviesGroupBeansInChain();
            } else {
                moviesGroupBean.setRemotePage(moviesGroupBean.getRemotePage() + 1);
                moviesGroupBean.load(moviesListBean);
            }
        }
    }
}