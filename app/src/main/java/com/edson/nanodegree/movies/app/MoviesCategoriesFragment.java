package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MoviesListBean;
import com.edson.nanodegree.movies.factory.MoviesListFactory;
import com.edson.nanodegree.movies.service.LoadMoviesDiscoveryService;
import com.edson.nanodegree.movies.util.MoviesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by edson on 15/10/2017.
 */

public class MoviesCategoriesFragment extends AbstractMoviesListFragment {

    private static final String LOG_TAG = MoviesCategoriesFragment.class.getSimpleName();

    protected String[] genresNames;
    protected String[] genresValues;
    protected Set<String> genresValuesSelected;

    protected SharedPreferences preferences;

    protected LoadMoviesDiscoveryService loadMovies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Load the sort value saved or default
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        genresNames = getResources().getStringArray(R.array.movie_api_genres_pref_list);
        genresValues = getResources().getStringArray(R.array.movie_api_genres_list_ids);
        genresValuesSelected = preferences.getStringSet(getResources().getString(R.string.preference_genres_list_key),
                new LinkedHashSet<>(Arrays.asList(getResources().getStringArray(R.array.movie_api_genres_pref_list_defaults))));
        for(String gv : genresValuesSelected){
            Log.i(LOG_TAG, " - genresValuesSelected > " + gv);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected MoviesListBean createMoviesListBean() {
        return MoviesListFactory.createMoviesListCategories(getContext(), genresNames, genresValues, genresValuesSelected);
    }

    @Override
    public void init() {
        super.init();

        loadMovies = new LoadMoviesDiscoveryService(getContext());
        loadMovies.setSortBySelected(preferences.getString(
                LoadMoviesDiscoveryService.SORT_BY_KEY,
                LoadMoviesDiscoveryService.SORT_BY_POPULARITY));

        moviesListBean.setLoadMovies(loadMovies);
    }

    @Override
    public void loadMoviesGroupBeansInit() {
        String headerTitle = "";
        if(loadMovies.isPopularity()){
            headerTitle = getResources().getString(R.string.app_most_popular);
        }else if(loadMovies.isRate()){
            headerTitle = getResources().getString(R.string.app_most_rate);
        }else if(loadMovies.isUpcoming()){
            headerTitle = getResources().getString(R.string.app_upcoming);
        }else if(loadMovies.isNowPlaying()){
            headerTitle = getResources().getString(R.string.app_now_playing);
        }
        moviesListBean.getMoviesGroupBeans().get(0).setTitle(headerTitle);
        floatingHeader.setText(moviesListBean.getMoviesGroupBeans().get(0).getTitle());

        for(int i = 1; i < categoryViews.length; i++){
            moviesListBean.getMoviesGroupBeans().get(i).setTitle(getResources().getString(R.string.movie_api_sortby_genres)
                    .replace("{0}", headerTitle)
                    .replace("{1}", moviesListBean.getMoviesGroupBeans().get(i).getGroupName()));
            TextView text = (TextView) categoryViews[i].findViewById(R.id.title);
            text.setText(moviesListBean.getMoviesGroupBeans().get(i).getTitle());
        }

        super.loadMoviesGroupBeansInit();

    }

    @Override
    public void onStart() {
        super.onStart();

        if(validateGenresValuesChanged()){
            reset();
        }
    }

    protected boolean validateGenresValuesChanged(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> genresValuesSelectedCurrent = prefs.getStringSet(getResources().getString(R.string.preference_genres_list_key),
                new LinkedHashSet<>(Arrays.asList(getResources().getStringArray(R.array.movie_api_genres_pref_list_defaults))));

        boolean changed = true;

        Log.i(LOG_TAG, "Sizes a: " + genresValuesSelectedCurrent.size() + ", b: " + genresValuesSelected.size());

        if(genresValuesSelectedCurrent.size() == genresValuesSelected.size()){
            Iterator<String> ia = genresValuesSelectedCurrent.iterator();
            Iterator<String> ib = genresValuesSelected.iterator();
            changed = false;
            while(ia.hasNext()){
                String a = ia.next();
                String b = ib.next();
                Log.i(LOG_TAG, "Values a: " + a + ", b: " + b);
                if(!a.equals(b)){
                    changed = true;
                    break;
                }
            }
        }

        Log.i(LOG_TAG, " result " + changed);

        if(changed){
            genresValuesSelected = genresValuesSelectedCurrent;
        }
        return changed;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_movies_main, menu);

        //ckeck the menuitem by sort
        if(loadMovies.isPopularity()){
            menu.findItem(R.id.item_popularity).setChecked(true);
        }else if(loadMovies.isRate()){
            menu.findItem(R.id.item_rate).setChecked(true);
        }else if(loadMovies.isUpcoming()){
            menu.findItem(R.id.item_upcoming).setChecked(true);
        }else if(loadMovies.isNowPlaying()){
            menu.findItem(R.id.item_now_playing).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();

        Log.i(LOG_TAG, "ItemId:" + id);

        //getting sort selected

        boolean makeReset = false;
        if (id == R.id.item_popularity){
            loadMovies.setSortBySelected(LoadMoviesDiscoveryService.SORT_BY_POPULARITY);
            makeReset = true;
        }else if (id == R.id.item_rate){
            loadMovies.setSortBySelected(LoadMoviesDiscoveryService.SORT_BY_RATE);
            makeReset = true;
        }else if (id == R.id.item_upcoming){
            loadMovies.setSortBySelected(LoadMoviesDiscoveryService.SORT_BY_UPCOMING);
            makeReset = true;
        }else if (id == R.id.item_now_playing){
            loadMovies.setSortBySelected(LoadMoviesDiscoveryService.SORT_BY_NOW_PLAYING);
            makeReset = true;
        }else if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_search){
            MoviesUtil.openMoviesSearchFragment(getActivity().getSupportFragmentManager());
        }

        if(makeReset){
            item.setChecked(true);
            prefs.putString(LoadMoviesDiscoveryService.SORT_BY_KEY, loadMovies.getSortBySelected());
            prefs.apply();

            reset();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
