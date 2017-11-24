package com.edson.nanodegree.movies.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.edson.nanodegree.movies.app.R;
import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.util.MoviesUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by edson on 21/11/2017.
 */

public class LoadMoviesDiscoveryService extends AbstractLoadMovies {

    private static final String LOG_TAG = LoadMoviesDiscoveryService.class.getSimpleName();

    public static final String SORT_BY_KEY = "SORT_BY";
    public static final String SORT_BY_POPULARITY = "POPULARITY";
    public static final String SORT_BY_RATE = "RATE";
    public static final String SORT_BY_UPCOMING = "UPCOMING";
    public static final String SORT_BY_NOW_PLAYING = "NOW_PLAYING";
    public static final int NOW_PLAYING_LAST_WEEK = 3;

    private final SimpleDateFormat moviesApiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private String param_urlBase;
    private String param_apiKeyParam;
    private String param_apiKeyValue;
    private String param_sortParam;
    private String param_sortPopularityDescValue;
    private String param_sortRateDescValue;
    private String param_page;
    private String param_languageParam;
    private String param_languageValue;
    private String param_genResParam;
    private String param_primaryReleaseDateGteParam;
    private String param_releaseDateGteParam;
    private String param_releaseDateLteParam;

    private String sortBySelected;

    private String sortValue;
    private String primaryReleaseDateGte;
    private String releaseDateGte;
    private String releaseDateLte;

    public LoadMoviesDiscoveryService(Context context) {
        super(context);
    }

    @Override
    public void initParameters(Context context) {
        param_urlBase = context.getResources().getString(R.string.movie_api_base_discovery_url);
        param_apiKeyParam = context.getResources().getString(R.string.movie_api_key_param);
        param_apiKeyValue = context.getResources().getString(R.string.movie_api_key_value);
        param_sortParam = context.getResources().getString(R.string.movie_api_sort_param);
        param_sortPopularityDescValue = context.getResources().getString(R.string.movie_api_sort_popularity_desc);
        param_sortRateDescValue = context.getResources().getString(R.string.movie_api_sort_rate_desc);
        param_page = context.getResources().getString(R.string.movie_api_page_param);
        param_languageParam = context.getResources().getString(R.string.movie_api_language_param);
        param_languageValue = context.getResources().getString(R.string.movie_api_language_value);
        param_genResParam = context.getResources().getString(R.string.movie_api_genres_param);
        param_primaryReleaseDateGteParam = context.getResources().getString(R.string.movie_api_primary_release_date_gte);
        param_releaseDateGteParam = context.getResources().getString(R.string.movie_api_release_date_gte);
        param_releaseDateLteParam = context.getResources().getString(R.string.movie_api_release_date_lte);
    }

    @Override
    public void loadMovies(MoviesGroupBean moviesGroupBean){
        Uri.Builder builder = Uri.parse(param_urlBase).buildUpon()
                .appendQueryParameter(param_apiKeyParam, param_apiKeyValue)
                .appendQueryParameter(param_languageParam, param_languageValue)
                .appendQueryParameter(param_sortParam, sortValue)
                .appendQueryParameter(param_page, String.valueOf(moviesGroupBean.getRemotePage()));
        if (moviesGroupBean.getId() != null) {
            builder.appendQueryParameter(param_genResParam, String.valueOf(moviesGroupBean.getId()));
        }
        if(primaryReleaseDateGte != null){
            builder.appendQueryParameter(param_primaryReleaseDateGteParam, primaryReleaseDateGte);
        }
        if(releaseDateGte != null){
            builder.appendQueryParameter(param_releaseDateGteParam, releaseDateGte);
        }
        if(releaseDateLte != null){
            builder.appendQueryParameter(param_releaseDateLteParam, releaseDateLte);
        }
        Log.i(LOG_TAG, "Url: " + builder.build().toString());

        String jsonResult = MoviesUtil.getJsonResultURL(builder.build().toString());
        MoviesUtil.loadMoviesFromDiscoveryJson(jsonResult, moviesGroupBean);
    }

    public boolean isPopularity(){
        return sortBySelected.equals(SORT_BY_POPULARITY);
    }

    public boolean isRate(){
        return sortBySelected.equals(SORT_BY_RATE);
    }

    public boolean isUpcoming(){
        return sortBySelected.equals(SORT_BY_UPCOMING);
    }

    public boolean isNowPlaying(){
        return sortBySelected.equals(SORT_BY_NOW_PLAYING);
    }

    public void setSortBySelected(String sortBySelected) {
        this.sortBySelected = sortBySelected;

        Calendar now = Calendar.getInstance();

        if (isPopularity()){
            sortValue = param_sortPopularityDescValue;
            primaryReleaseDateGte = null;
            releaseDateGte = null;
            releaseDateLte = null;
        }else if (isRate()){
            sortValue = param_sortRateDescValue;
            primaryReleaseDateGte = null;
            releaseDateGte = null;
            releaseDateLte = null;
        }else if (isUpcoming()) {
            sortValue = param_sortPopularityDescValue;
            primaryReleaseDateGte = moviesApiDateFormat.format(now.getTime());
            releaseDateGte = null;
            releaseDateLte = null;
        }else if (isNowPlaying()) {
            sortValue = param_sortPopularityDescValue;
            primaryReleaseDateGte = null;
            releaseDateLte = moviesApiDateFormat.format(now.getTime());
            now.add(Calendar.DATE, NOW_PLAYING_LAST_WEEK * (-7));
            releaseDateGte = moviesApiDateFormat.format(now.getTime());
        }
    }

    public String getSortBySelected() {
        return sortBySelected;
    }
}
