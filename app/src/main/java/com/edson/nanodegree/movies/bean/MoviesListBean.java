package com.edson.nanodegree.movies.bean;

import android.util.Log;

import com.edson.nanodegree.movies.service.MoviesListClientRestTask;

import java.util.List;

/**
 * Created by edson on 21/09/2017.
 */

public abstract class MoviesListBean {

    private final String LOG_TAG = MoviesListBean.class.getSimpleName();

    private List<MoviesGroupBean> moviesGroupBeans;
    private int index;

    public abstract void init();

    public abstract void reset();

    public abstract void generateUrls();

    public void loadMoviesGroupBeansInit(){
        index = -1;
        generateUrls();
        loadMoviesGroupBeansInChain();
    }

    public void loadMoviesGroupBeansInChain(){
        if(index < moviesGroupBeans.size() - 1) {
            index++;

            MoviesGroupBean moviesGroupBean = moviesGroupBeans.get(index);
            Log.i(LOG_TAG, "LOAD: " + moviesGroupBean.getTitle() + ", active: " + moviesGroupBean.isActive());

            if(moviesGroupBean.isActive()) {
                moviesGroupBean.setRemotePage(moviesGroupBean.getRemotePage() + 1);
                callMoviesListClientRestTask(moviesGroupBean);
            }else{
                loadMoviesGroupBeansInChain();
            }
        }
    }

    public void callMoviesListClientRestTask(MoviesGroupBean moviesGroupBean){
        new MoviesListClientRestTask(this, moviesGroupBean.getMovies().size()).execute(moviesGroupBean);
    }

    public List<MoviesGroupBean> getMoviesGroupBeans() {
        return moviesGroupBeans;
    }

    public void setMoviesGroupBeans(List<MoviesGroupBean> moviesGroupBeans) {
        this.moviesGroupBeans = moviesGroupBeans;
    }

}
