package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MoviesListBean;
import com.edson.nanodegree.movies.util.MoviesActivityUtil;

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

    protected String sortValue;
    protected String[] genresNames;
    protected String[] genresValues;
    protected Set<String> genresValuesSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Load the sort value saved or default
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        sortValue = prefs.getString(getResources().getString(R.string.movie_api_sort_param), getResources().getString(R.string.movie_api_sort_popularity_desc));

        genresNames = getResources().getStringArray(R.array.movie_api_genres_pref_list);
        genresValues = getResources().getStringArray(R.array.movie_api_genres_list_ids);
        genresValuesSelected = prefs.getStringSet(getResources().getString(R.string.preference_genres_list_key),
                new LinkedHashSet<>(Arrays.asList(getResources().getStringArray(R.array.movie_api_genres_pref_list_defaults))));
        for(String gv : genresValuesSelected){
            Log.i(LOG_TAG, " - genresValuesSelected > " + gv);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected MoviesListBean initMoviesListBean() {
        return new MoviesListBean() {
            @Override
            public void init() {
                List<MoviesGroupBean> moviesGroupBeans = new ArrayList<>();

                // all genres
                moviesGroupBeans.add(new MoviesGroupBean(getActivity(), "", null));

                // adding genres
                for (int i = 0; i < genresValues.length; i++) {
                    String name = genresNames[i];
                    String id = genresValues[i];
                    boolean isActive = genresValuesSelected.contains(id);

                    Log.i(LOG_TAG, " - name: " + name + ", id: " + id + ", isActive: " + isActive);

                    MoviesGroupBean moviesGroupBean = new MoviesGroupBean(getActivity(), name, Integer.parseInt(id));
                    moviesGroupBean.setActive(isActive);
                    moviesGroupBeans.add(moviesGroupBean);
                }

                this.setMoviesGroupBeans(moviesGroupBeans);
            }

            @Override
            public void reset() {
                super.reset();

                // adding genres
                for(int i = 1; i < getMoviesGroupBeans().size(); i++){
                    boolean isActive = genresValuesSelected.contains(String.valueOf(getMoviesGroupBeans().get(i).getId()));
                    getMoviesGroupBeans().get(i).setActive(isActive);
                }
            }

            @Override
            public void generateUrls() {

                final String URL_BASE = getResources().getString(R.string.movie_api_base_discovery_url);
                final String API_KEY_PARAM = getResources().getString(R.string.movie_api_key_param);
                final String API_KEY_VALUE = getResources().getString(R.string.movie_api_key_value);
                final String SORT_PARAM = getResources().getString(R.string.movie_api_sort_param);
                final String PAGE = getResources().getString(R.string.movie_api_page_param);
                final String LANGUAGE_PARAM = getResources().getString(R.string.movie_api_language_param);
                final String LANGUAGE_VALUE = getResources().getString(R.string.movie_api_language_value);
                final String GEN_RES_PARAM = getResources().getString(R.string.movie_api_genres_param);

                for(MoviesGroupBean moviesGroupBean : getMoviesGroupBeans()){
                    Uri.Builder builder = Uri.parse(URL_BASE).buildUpon()
                            .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                            .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)
                            .appendQueryParameter(SORT_PARAM, sortValue);
                    if (moviesGroupBean.getId() != null) {
                        builder.appendQueryParameter(GEN_RES_PARAM, String.valueOf(moviesGroupBean.getId()));
                    }
                    moviesGroupBean.setUriBuilder(builder);
                    moviesGroupBean.setPageParameter(PAGE);
                }
            }

        };
    }

    @Override
    public void loadMoviesGroupBeansInit() {
        String headerTitle;
        if(sortValue.equals(getResources().getString(R.string.movie_api_sort_popularity_desc))){
            headerTitle = getResources().getString(R.string.app_most_popular);
        }else{
            headerTitle = getResources().getString(R.string.app_most_rate);
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
        inflater.inflate(R.menu.menu_movies, menu);

        //ckeck the menuitem by sort
        if(sortValue.equals(getResources().getString(R.string.movie_api_sort_rate_desc))){
            menu.findItem(R.id.item_rate).setChecked(true);
        }else{
            menu.findItem(R.id.item_popularity).setChecked(true);
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
        String sortValueTemp = null;
        if (id == R.id.item_popularity){
            sortValueTemp = getResources().getString(R.string.movie_api_sort_popularity_desc);
        }else if (id == R.id.item_rate){
            sortValueTemp = getResources().getString(R.string.movie_api_sort_rate_desc);
        }else if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_search){
            MoviesActivityUtil.openMoviesSearchFragment(getActivity().getSupportFragmentManager());
        }

        if(sortValueTemp != null){
            item.setChecked(true);
            if(sortValue != sortValueTemp) {
                sortValue = sortValueTemp;
                prefs.putString(getResources().getString(R.string.movie_api_sort_param), sortValue);
                prefs.apply();

                reset();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
