package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.edson.nanodegree.movies.bean.MovieBean;
import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MoviesListBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by edson on 15/10/2017.
 */

public class MoviesCategoriesFragment extends Fragment {

    private static final String LOG_TAG = MoviesCategoriesFragment.class.getSimpleName();

    protected MoviesListBean moviesCategoriesBean;

    protected LinearLayout layoutContent;
    protected ScrollView moviesScroll;
    protected int scrollIndex;
    protected View[] categoryViews;
    protected TextView floatingHeader;

    protected String sortValue;
    protected String[] genresNames;
    protected String[] genresValues;
    protected Set<String> genresValuesSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_movies, container, false);

        layoutContent = (LinearLayout) rootView.findViewById(R.id.movies_layout);
        moviesScroll = (ScrollView) rootView.findViewById(R.id.movies_scroll);
        floatingHeader = (TextView) rootView.findViewById(R.id.floating_header);

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

        this.setHasOptionsMenu(true);

        moviesScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                if(categoryViews != null){
                    int i = 0;
                    for (View v : categoryViews) {
                        MoviesGroupBean moviesGroupBeans = moviesCategoriesBean.getMoviesGroupBeans().get(i);
                        if(moviesGroupBeans.isActive()) {
                            if (moviesScroll.getScrollY() > v.getTop() + floatingHeader.getHeight() / 2 && i != scrollIndex) {
                                floatingHeader.setText(moviesGroupBeans.getTitle());
                                scrollIndex = i;
                            }
                        }
                        i++;
                    }
                }
            }

        });

        moviesCategoriesBean = new MoviesListBean() {
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
                for(MoviesGroupBean moviesGroupBean : getMoviesGroupBeans()){
                    moviesGroupBean.reset();
                }

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

        moviesCategoriesBean.init();

        initViews();
        loadCategoriesBeansInit();

        return rootView;
    }

    protected void initViews(){
        // load a layoutContent for each category
        categoryViews = new LinearLayout[moviesCategoriesBean.getMoviesGroupBeans().size()];
        for (int i = 0; i < moviesCategoriesBean.getMoviesGroupBeans().size(); i++) {
            categoryViews[i] = getViewForMoviesGroup(layoutContent, moviesCategoriesBean.getMoviesGroupBeans().get(i), i);
        }
    }

    public void loadCategoriesBeansInit() {
        String headerTitle;
        if(sortValue.equals(getResources().getString(R.string.movie_api_sort_popularity_desc))){
            headerTitle = getResources().getString(R.string.app_most_popular);
        }else{
            headerTitle = getResources().getString(R.string.app_most_rate);
        }
        moviesCategoriesBean.getMoviesGroupBeans().get(0).setTitle(headerTitle);
        floatingHeader.setText(moviesCategoriesBean.getMoviesGroupBeans().get(0).getTitle());

        for(int i = 1; i < categoryViews.length; i++){
            moviesCategoriesBean.getMoviesGroupBeans().get(i).setTitle(getResources().getString(R.string.movie_api_sortby_genres)
                    .replace("{0}", headerTitle)
                    .replace("{1}", moviesCategoriesBean.getMoviesGroupBeans().get(i).getGroupName()));
            TextView text = (TextView) categoryViews[i].findViewById(R.id.title);
            text.setText(moviesCategoriesBean.getMoviesGroupBeans().get(i).getTitle());
        }

        scrollIndex = -1;
        moviesScroll.fullScroll(ScrollView.FOCUS_UP);

        moviesCategoriesBean.loadMoviesGroupBeansInit();
    }

    public void reset(){
        moviesCategoriesBean.reset();
        resetViews();
        loadCategoriesBeansInit();
    }

    protected void resetViews(){
        for(int i = 1; i < moviesCategoriesBean.getMoviesGroupBeans().size(); i++) {
            categoryViews[i].setActivated(moviesCategoriesBean.getMoviesGroupBeans().get(i).isActive());
            categoryViews[i].setVisibility(moviesCategoriesBean.getMoviesGroupBeans().get(i).isActive() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        validateGenresValuesChanged();
    }

    protected void validateGenresValuesChanged(){

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

        Log.i(LOG_TAG, "Result " + changed);

        if(changed){
            genresValuesSelected = genresValuesSelectedCurrent;
            reset();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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

    protected View getViewForMoviesGroup(ViewGroup viewGroup, final MoviesGroupBean moviesGroupBean, int index){
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_grid_section, null);

        convertView.setActivated(moviesGroupBean.isActive());
        convertView.setVisibility(moviesGroupBean.isActive() ? View.VISIBLE : View.GONE);

        TextView text = (TextView)convertView.findViewById(R.id.title);
        text.setText(moviesGroupBean.getTitle());
        if(index == 0){
            text.setHeight(0);
        }

        MyGridView grid = (MyGridView)convertView.findViewById(R.id.grid);
        grid.setAdapter(moviesGroupBean.getAdapter());
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieBean movie = moviesGroupBean.getAdapter().getMovies().get(position);
                Intent intent = new Intent(getActivity(), MoviesDetailActivity.class);
                intent.putExtras(movie.getBundle());
                startActivity(intent);
            }
        });

        Button button = (Button)convertView.findViewById(R.id.button);
        Typeface fontFace = Typeface.createFromAsset(getActivity().getAssets(), getResources().getString(R.string.font_lqdkdz_nospace));
        button.setTypeface(fontFace);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moviesGroupBean.setCurrentPage(moviesGroupBean.getCurrentPage() + 1);
                if(!moviesGroupBean.validateLoadMoviesPageComplete()){
                    moviesGroupBean.setRemotePage(moviesGroupBean.getRemotePage() + 1);
                    moviesCategoriesBean.callMoviesListClientRestTask(moviesGroupBean);
                }
            }
        });

        viewGroup.addView(convertView);
        return convertView;

    }

}
