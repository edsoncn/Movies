package com.edson.nanodegree.movies.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.edson.nanodegree.movies.app.R;
import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.util.MoviesUtil;

/**
 * Created by edson on 21/11/2017.
 */

public class LoadMoviesSearchService extends AbstractLoadMovies{

    private static final String LOG_TAG = LoadMoviesSearchService.class.getSimpleName();

    private String param_urlBase;
    private String param_apiKeyParam;
    private String param_apiKeyValue;
    private String param_page;
    private String param_languageParam;
    private String param_languageValue;
    private String param_queryParam;

    private String searchTerm;

    public LoadMoviesSearchService(Context context) {
        super(context);
    }

    @Override
    void initParameters(Context context) {
        param_urlBase = context.getResources().getString(R.string.movie_api_base_search_url);
        param_apiKeyParam = context.getResources().getString(R.string.movie_api_key_param);
        param_apiKeyValue = context.getResources().getString(R.string.movie_api_key_value);
        param_page = context.getResources().getString(R.string.movie_api_page_param);
        param_languageParam = context.getResources().getString(R.string.movie_api_language_param);
        param_languageValue = context.getResources().getString(R.string.movie_api_language_value);
        param_queryParam = context.getResources().getString(R.string.movie_api_query_param);
    }

    @Override
    public void loadMovies(MoviesGroupBean moviesGroupBean){
        Uri.Builder builder = Uri.parse(param_urlBase).buildUpon()
                .appendQueryParameter(param_apiKeyParam, param_apiKeyValue)
                .appendQueryParameter(param_languageParam, param_languageValue)
                .appendQueryParameter(param_queryParam, searchTerm)
                .appendQueryParameter(param_page, String.valueOf(moviesGroupBean.getRemotePage()));
        Log.i(LOG_TAG, "Url: " + builder.build().toString());

        String jsonResult = MoviesUtil.getJsonResultURL(builder.build().toString());
        MoviesUtil.loadMoviesFromDiscoveryJson(jsonResult, moviesGroupBean);
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
