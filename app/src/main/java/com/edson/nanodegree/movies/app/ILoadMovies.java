package com.edson.nanodegree.movies.app;

import com.edson.nanodegree.movies.bean.CategoryBean;

/**
 * Created by edson on 15/09/2015.
 *
 * Interface for call the asynkTask of MoviesActivityFragment
 */
public interface ILoadMovies {

    void loadCategoryMoviesBeanInChainInit();

    void loadCategoryMoviesBeanInChain(CategoryBean categoryBean);

}
