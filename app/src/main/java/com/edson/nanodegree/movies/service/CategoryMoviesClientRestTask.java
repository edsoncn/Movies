package com.edson.nanodegree.movies.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.edson.nanodegree.movies.bean.CategoriesListBean;
import com.edson.nanodegree.movies.bean.CategoryBean;
import com.edson.nanodegree.movies.util.MoviesActivityUtil;

/**
 * Created by edson on 12/10/2017.
 */

public class CategoryMoviesClientRestTask extends AsyncTask<CategoryBean, Void, CategoryBean> {

    private final String LOG_TAG = CategoryMoviesClientRestTask.class.getSimpleName();

    private CategoriesListBean categoriesListBean;

    public CategoryMoviesClientRestTask(CategoriesListBean categoriesListBean){
        this.categoriesListBean = categoriesListBean;
    }

    @Override
    protected CategoryBean doInBackground(CategoryBean... params) {

        CategoryBean categoryBean = params[0];
        categoryBean.setRemotePage(categoryBean.getRemotePage() + 1);
        Log.i(LOG_TAG, "Load: " + categoryBean.getTitle());

        Uri uri = categoryBean.getUri();
        Log.i(LOG_TAG, "Url: " + uri.toString());

        String jsonResult = MoviesActivityUtil.getJsonResultURL(uri.toString());
        return MoviesActivityUtil.loadMoviesFromDiscoveryJson(jsonResult, categoryBean);
    }

    @Override
    protected void onPostExecute(CategoryBean category) {
        Log.i(LOG_TAG, "Movies list size: " + category.getMovies().size());
        Log.i(LOG_TAG, "Movies list temp size: " + category.getMoviesTemp().size());
        Log.i(LOG_TAG, "Real and current: " + category.getRealPage() + ", " + category.getCurrentPage());
        if(category.validateLoadMovies()){
            categoriesListBean.loadCetegoryBeanInChain();
        }
    }
}