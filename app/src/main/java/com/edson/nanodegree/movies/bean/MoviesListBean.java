package com.edson.nanodegree.movies.bean;

import android.util.Log;

import java.util.List;

/**
 * Created by edson on 21/09/2017.
 */

public abstract class MoviesListBean {

    private final String LOG_TAG = MoviesListBean.class.getSimpleName();

    private List<MoviesGroupBean> moviesGroupBeans;
    private int index;
    private int state;

    public MoviesListBean(){
    }

    public abstract void init();

    public void reset(){
        for(MoviesGroupBean moviesGroupBean : getMoviesGroupBeans()){
            moviesGroupBean.reset();
        }
    }

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
                moviesGroupBean.load(this);
            }else{
                loadMoviesGroupBeansInChain();
            }
        }
    }

    public List<MoviesGroupBean> getMoviesGroupBeans() {
        return moviesGroupBeans;
    }

    public void setMoviesGroupBeans(List<MoviesGroupBean> moviesGroupBeans) {
        this.moviesGroupBeans = moviesGroupBeans;
    }

    public int getState() {
        return state;
    }
}
