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
    public static final int NOW_PLAYING_LASTED_WEEK = 4;

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
    private String param_primaryReleaseDateLteParam;
    private String param_certificationCountryParam;
    private String param_certificationCountryValue;
    private String param_certificationLteParam;
    private String param_certificationNC17Value;

    private String sortBySelected;

    private String sortValue;
    private String primaryReleaseDateGte;
    private String primaryReleaseDateLte;

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
        param_primaryReleaseDateLteParam = context.getResources().getString(R.string.movie_api_primary_release_date_lte);
        param_certificationCountryParam = context.getResources().getString(R.string.movie_api_certification_country_param);
        param_certificationCountryValue = context.getResources().getString(R.string.movie_api_certification_country_value);
        param_certificationLteParam = context.getResources().getString(R.string.movie_api_certification_lte_param);
        param_certificationNC17Value = context.getResources().getString(R.string.movie_api_certification_nc17_value);
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
        if(primaryReleaseDateLte != null){
            builder.appendQueryParameter(param_primaryReleaseDateLteParam, primaryReleaseDateLte);
        }
        if(isRate()){
            builder.appendQueryParameter(param_certificationCountryParam, param_certificationCountryValue);
            builder.appendQueryParameter(param_certificationLteParam, param_certificationNC17Value);
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
        setNullAllValues();

        if (isPopularity()){
            sortValue = param_sortPopularityDescValue;
        }else if (isRate()){
            sortValue = param_sortRateDescValue;
        }else if (isUpcoming()) {
            sortValue = param_sortPopularityDescValue;
            primaryReleaseDateGte = moviesApiDateFormat.format(now.getTime());
        }else if (isNowPlaying()) {
            sortValue = param_sortPopularityDescValue;
            primaryReleaseDateLte = moviesApiDateFormat.format(now.getTime());
            now.add(Calendar.DATE, NOW_PLAYING_LASTED_WEEK * (-7));
            primaryReleaseDateGte = moviesApiDateFormat.format(now.getTime());
        }
    }

    private void setNullAllValues(){
        sortValue = null;
        primaryReleaseDateGte = null;
        primaryReleaseDateLte = null;
    }

    public String getSortBySelected() {
        return sortBySelected;
    }
}
