package com.edson.nanodegree.movies.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.edson.nanodegree.movies.bean.MoviesListBean;
import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.util.MoviesActivityUtil;

/**
 * Created by edson on 12/10/2017.
 */

public class MoviesGroupClientRestTask extends AsyncTask<MoviesGroupBean, Void, MoviesGroupBean> {

    private final String LOG_TAG = MoviesGroupClientRestTask.class.getSimpleName();

    private MoviesListBean moviesListBean;
    private int moviesSize;

    public MoviesGroupClientRestTask(MoviesListBean moviesListBean, int moviesSize){
        this.moviesListBean = moviesListBean;
        this.moviesSize = moviesSize;
    }

    @Override
    protected MoviesGroupBean doInBackground(MoviesGroupBean... params) {

        MoviesGroupBean moviesGroupBean = params[0];
        Log.i(LOG_TAG, "Load: " + moviesGroupBean.getTitle());

        Uri.Builder builder = moviesGroupBean.getUriBuilder();
        builder.appendQueryParameter(moviesGroupBean.getPageParameter(), String.valueOf(moviesGroupBean.getRemotePage()));
        Log.i(LOG_TAG, "Url: " + builder.build().toString());

        String jsonResult = MoviesActivityUtil.getJsonResultURL(builder.build().toString());
        moviesGroupBean.addMoviesFromJson(jsonResult);
        return moviesGroupBean;
    }

    @Override
    protected void onPostExecute(MoviesGroupBean moviesGroupBean) {
        Log.i(LOG_TAG, "Movies list size: " + moviesGroupBean.getMovies().size());
        Log.i(LOG_TAG, "Movies list temp size: " + moviesGroupBean.getMoviesTemp().size());
        Log.i(LOG_TAG, "Real and current: " + moviesGroupBean.getRealPage() + ", " + moviesGroupBean.getCurrentPage());

        if(moviesGroupBean.validateLoadMoviesPageComplete(moviesSize)){
            moviesListBean.loadMoviesGroupBeansInChain();
        }else{
            moviesGroupBean.setRemotePage(moviesGroupBean.getRemotePage() + 1);
            moviesGroupBean.load(moviesListBean);
        }
    }
}