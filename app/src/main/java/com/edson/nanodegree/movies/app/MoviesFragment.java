package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MovieBean;
import com.edson.nanodegree.movies.util.MoviesActivityUtil;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment{

    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private LinearLayout layoutContent;
    private ScrollView moviesScroll;
    private View[] categoryViews;
    private TextView floatingHeader;

    private MoviesGroupBean[] moviesGroupBeans;
    private int arrayCategoryIndex;
    private int scrollIndex;

    private String sortValue;

    private String[] genresNames;
    private String[] genresValues;
    private Set<String> genresValuesSelected;

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
                        if(moviesGroupBeans[i].isActive()) {
                            if (moviesScroll.getScrollY() > v.getTop() + floatingHeader.getHeight() / 2 && i != scrollIndex) {
                                floatingHeader.setText(moviesGroupBeans[i].getTitle());
                                scrollIndex = i;
                            }
                        }
                        i++;
                    }
                }
            }
        });

        loadMovies();

        return rootView;
    }

    private void loadMovies(){
        loadMoviesFromPreference();
    }

    private void loadMoviesCategoryTask(){
        //Load the categories with asynktask
        new MovieGenresTask().execute();
    }

    private void loadMoviesFromPreference(){

        moviesGroupBeans = new MoviesGroupBean[genresValues.length + 1];

        // all genres
        moviesGroupBeans[0] = new MoviesGroupBean(getActivity(), "", null);

        // adding genres
        for(int i = 0; i < genresValues.length; i++){
            String name = genresNames[i];
            String id = genresValues[i];
            boolean isActive = genresValuesSelected.contains(id);

            Log.i(LOG_TAG, " - name: " + name + ", id: " + id + ", isActive: " + isActive);

            moviesGroupBeans[i + 1] = new MoviesGroupBean(getActivity(), name, Integer.parseInt(id));
            moviesGroupBeans[i + 1].setActive(isActive);
        }

        loadCategoriesBeansViews();

        loadCategoryMoviesBeanInChainInit();

    }

    private void loadCategoriesBeans(Map<String, Integer> mapResult){

        moviesGroupBeans = new MoviesGroupBean[mapResult.size() + 1];

        // all genres
        moviesGroupBeans[0] = new MoviesGroupBean(getActivity(), "", null);

        // adding genres
        int i = 1;
        for(Map.Entry<String, Integer> entry : mapResult.entrySet()){
            String name = entry.getKey();
            Integer id = entry.getValue();
            boolean isActive = genresValuesSelected.contains(String.valueOf(id));

            Log.i(LOG_TAG, " - name: " + name + ", id: " + id + ", isActive: " + isActive);

            moviesGroupBeans[i] = new MoviesGroupBean(getActivity(), name, id);
            moviesGroupBeans[i].setActive(isActive);
            i++;
        }

        loadCategoriesBeansViews();

        loadCategoryMoviesBeanInChainInit();

    }

    private void loadCategoriesBeansViews(){

        // load a layoutContent for each category
        categoryViews = new LinearLayout[moviesGroupBeans.length];
        for (int i = 0; i < moviesGroupBeans.length; i++) {
            categoryViews[i] = getViewForCategory(layoutContent, moviesGroupBeans[i], i);
        }

    }

    public void loadCategoryMoviesBeanInChainInit(){
        String headerTitle;
        if(sortValue.equals(getResources().getString(R.string.movie_api_sort_popularity_desc))){
            headerTitle = getResources().getString(R.string.app_most_popular);
        }else{
            headerTitle = getResources().getString(R.string.app_most_rate);
        }
        moviesGroupBeans[0].setTitle(headerTitle);
        floatingHeader.setText(moviesGroupBeans[0].getTitle());

        for(int i = 1; i < categoryViews.length; i++){
            moviesGroupBeans[i].setTitle(getResources().getString(R.string.movie_api_sortby_genres)
                    .replace("{0}", headerTitle)
                    .replace("{1}", moviesGroupBeans[i].getGroupName()));
            TextView text = (TextView)categoryViews[i].findViewById(R.id.title);
            text.setText(moviesGroupBeans[i].getTitle());
        }

        arrayCategoryIndex = 0;
        scrollIndex = -1;
        moviesScroll.fullScroll(ScrollView.FOCUS_UP);

        // we will start loading the first category with async task,
        // then continues with the next category until the final category
        loadCategoryMoviesBeanInChain(moviesGroupBeans[arrayCategoryIndex]);
    }

    public void loadCategoryMoviesBeanInChain(MoviesGroupBean moviesGroupBean) {
        Log.i(LOG_TAG, "LOAD: " + moviesGroupBean.getTitle());
        if(moviesGroupBean.isActive()) {
            new CategoryMoviesClientRestTask().execute(moviesGroupBean);
        }else if (arrayCategoryIndex < moviesGroupBeans.length - 1) {
            arrayCategoryIndex++;
            loadCategoryMoviesBeanInChain(moviesGroupBeans[arrayCategoryIndex]);
        }
    }

    public void resetMovies(){
        resetCategoriesBean();
        resetCategoriesBeanviews();
        loadCategoryMoviesBeanInChainInit();
    }

    private void resetCategoriesBean(){

        for(MoviesGroupBean moviesGroupBean : moviesGroupBeans){
            moviesGroupBean.reset();
        }

        // adding genres
        for(int i = 1; i < moviesGroupBeans.length; i++){
            boolean isActive = genresValuesSelected.contains(String.valueOf(moviesGroupBeans[i].getId()));
            moviesGroupBeans[i].setActive(isActive);
        }

    }

    private void resetCategoriesBeanviews(){
        for(int i = 1; i < moviesGroupBeans.length; i++) {
            categoryViews[i].setActivated(moviesGroupBeans[i].isActive());
            categoryViews[i].setVisibility(moviesGroupBeans[i].isActive() ? View.VISIBLE : View.GONE);
        }
    }

    public class MovieGenresTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            final String URL_BASE = getResources().getString(R.string.movie_api_base_genres_url);
            final String API_KEY_PARAM = getResources().getString(R.string.movie_api_key_param);
            final String API_KEY_VALUE = getResources().getString(R.string.movie_api_key_value);
            final String LANGUAGE_PARAM = getResources().getString(R.string.movie_api_language_param);
            final String LANGUAGE_VALUE = getResources().getString(R.string.movie_api_language_value);

            Uri.Builder builder = Uri.parse(URL_BASE).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                    .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE);

            Uri uri = builder.build();

            Log.i(LOG_TAG, "url: " + uri.toString());
            return MoviesActivityUtil.getJsonResultURL(uri.toString());
        }

        @Override
        protected void onPostExecute(String jsonResult) {
            try{
                Map<String, Integer> mapResult = MoviesActivityUtil.getMapResult(jsonResult);
                loadCategoriesBeans(mapResult);
            } catch (JSONException e){
                Log.i(LOG_TAG, "Error al cargar los generos");
            }
        }
    }

    public class CategoryMoviesClientRestTask extends AsyncTask<MoviesGroupBean, Void, MoviesGroupBean> {

        private final String LOG_TAG = CategoryMoviesClientRestTask.class.getSimpleName();

        @Override
        protected MoviesGroupBean doInBackground(MoviesGroupBean... params) {

            final String URL_BASE = getResources().getString(R.string.movie_api_base_discovery_url);
            final String API_KEY_PARAM = getResources().getString(R.string.movie_api_key_param);
            final String API_KEY_VALUE = getResources().getString(R.string.movie_api_key_value);
            final String SORT_PARAM = getResources().getString(R.string.movie_api_sort_param);
            final String PAGE = getResources().getString(R.string.movie_api_page_param);
            final String LANGUAGE_PARAM = getResources().getString(R.string.movie_api_language_param);
            final String LANGUAGE_VALUE = getResources().getString(R.string.movie_api_language_value);
            final String GEN_RES_PARAM = getResources().getString(R.string.movie_api_genres_param);

            MoviesGroupBean category = params[0];
            category.setRemotePage(category.getRemotePage() + 1);

            Uri.Builder builder = Uri.parse(URL_BASE).buildUpon()
                    .appendQueryParameter(PAGE, String.valueOf(category.getRemotePage()))
                    .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                    .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)
                    .appendQueryParameter(SORT_PARAM, sortValue);
            if (category.getId() != null) {
                builder.appendQueryParameter(GEN_RES_PARAM, String.valueOf(category.getId()));
            }
            Uri uri = builder.build();

            Log.i(LOG_TAG, "CATEGORY: " + category.getTitle().toUpperCase());
            Log.i(LOG_TAG, "url: " + uri.toString());
            String jsonResult = MoviesActivityUtil.getJsonResultURL(uri.toString());
            return MoviesActivityUtil.loadMoviesFromDiscoveryJson(jsonResult, category);
        }

        @Override
        protected void onPostExecute(MoviesGroupBean category) {
            Log.i(LOG_TAG, "moviesList: " + category.getMovies().size());
            Log.i(LOG_TAG, "moviesListTemp: " + category.getMoviesTemp().size());
            Log.i(LOG_TAG, "real and current: " + category.getRealPage() + ", " + category.getCurrentPage());

            // Validate if the category is complete
            if(category.validateLoadMoviesPageComplete()){
                if(arrayCategoryIndex < moviesGroupBeans.length - 1) {
                    arrayCategoryIndex++;
                    loadCategoryMoviesBeanInChain(moviesGroupBeans[arrayCategoryIndex]);
                }
            }else{
                // Call again for make it complete
                loadCategoryMoviesBeanInChain(category);
            }

        }
    }

    private View getViewForCategory(ViewGroup viewGroup, final MoviesGroupBean category, int index){
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_grid_section, null);

        convertView.setActivated(category.isActive());
        convertView.setVisibility(category.isActive() ? View.VISIBLE : View.GONE);

        TextView text = (TextView)convertView.findViewById(R.id.title);
        text.setText(category.getTitle());
        if(index == 0){
            text.setHeight(0);
        }

        MyGridView grid = (MyGridView)convertView.findViewById(R.id.grid);
        grid.setAdapter(category.getAdapter());
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
            int position, long id) {
                MovieBean movie = category.getAdapter().getMovies().get(position);
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
                category.setCurrentPage(category.getCurrentPage() + 1);
                category.validateLoadMoviesPageComplete();
            }
        });

        viewGroup.addView(convertView);
        return convertView;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");

        validateGenresValuesChanged();
    }

    private void validateGenresValuesChanged(){

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
            resetMovies();
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

                resetMovies();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

}
