package com.edson.nanodegree.movies.service;

import android.content.Context;

/**
 * Created by edson on 21/11/2017.
 */

public abstract class AbstractLoadMovies implements LoadMovies {

    public AbstractLoadMovies(Context context){
        initParameters(context);
    }

    abstract void initParameters(Context context);

}
