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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesActivityFragment extends Fragment implements LoadMovies {

    private final String LOG_TAG = MoviesActivityFragment.class.getSimpleName();

    private LinearLayout layout;
    private ScrollView scroll;
    private CategoryBean[] arrayCategory;
    private LinearLayout[] layoutItem;
    private TextView floatingHeader;
    private int arrayCategoryIndex;
    private int scrollIndex;
    private String sortValue;

    public MoviesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        layout = (LinearLayout) rootView.findViewById(R.id.layout);
        scroll = (ScrollView) rootView.findViewById(R.id.scroll);
        floatingHeader = (TextView) rootView.findViewById(R.id.floatingHeader);

        // Load the sortvalue saved or dafault
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortValue = prefs.getString(getResources().getString(R.string.movie_api_sort_param),
                getResources().getString(R.string.movie_api_sort_popularity_desc));

        this.setHasOptionsMenu(true);

        //Load the categories with asynktask
        new MovieGenresTask().execute();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
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

        if (id == R.id.item_popularity){
            item.setChecked(true);
            String sortValueTemp = getResources().getString(R.string.movie_api_sort_popularity_desc);
            if(sortValue != sortValueTemp) {
                sortValue = getResources().getString(R.string.movie_api_sort_popularity_desc);
                prefs.putString(getResources().getString(R.string.movie_api_sort_param), sortValue);
                prefs.apply();
                resetCategories();
            }
            return true;
        }else if (id == R.id.item_rate){
            item.setChecked(true);
            String sortValueTemp = getResources().getString(R.string.movie_api_sort_rate_desc);
            if(sortValue != sortValueTemp) {
                sortValue = getResources().getString(R.string.movie_api_sort_rate_desc);
                prefs.putString(getResources().getString(R.string.movie_api_sort_param), sortValue);
                prefs.apply();
                resetCategories();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void resetCategories(){
        for(CategoryBean categoryBean : arrayCategory){
            categoryBean.reset();
        }
        loadCategorysChain();
    }

    /**
     * Implement loadMovie by category
     * */
    @Override
    public void loadMoview(CategoryBean categoryBean) {
        Log.i(LOG_TAG, "LOAD: " + categoryBean.getTitulo());
        new MovieDiscoveryTask().execute(categoryBean);
    }

    private void loadCategorys(Map map){

        String[] genres = getResources().getStringArray(R.array.movie_api_genres_list);

        arrayCategory = new CategoryBean[genres.length + 1];
        arrayCategory[0] = new CategoryBean(getActivity(), "",this, null);
        int i = 1;
        for (String name : genres){
            Integer id = (Integer)map.get(name);
            arrayCategory[i] = new CategoryBean(getActivity(), name, this, id);
            i++;
        }

        layoutItem = new LinearLayout[arrayCategory.length];
        for (i = 0; i < arrayCategory.length; i++) {
            layoutItem[i] = (LinearLayout)getViewForCategory(layout, arrayCategory[i], i);
        }

        loadCategorysChain();

        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int i = 0;
                for (View v : layoutItem) {
                    if (scroll.getScrollY() > v.getTop() && i != scrollIndex) {
                        floatingHeader.setText(arrayCategory[i].getTitulo());
                        scrollIndex = i;
                    }
                    i++;
                }
            }
        });
    }

    public void loadCategorysChain(){
        String floatingHeaderTitle;
        if(sortValue.equals(getResources().getString(R.string.movie_api_sort_popularity_desc))){
            floatingHeaderTitle = getResources().getString(R.string.app_most_popular);
        }else{
            floatingHeaderTitle = getResources().getString(R.string.app_most_rate);
        }
        arrayCategory[0].setTitulo(floatingHeaderTitle);
        floatingHeader.setText(arrayCategory[0].getTitulo());
        arrayCategoryIndex = 0;
        scrollIndex = -1;
        scroll.fullScroll(ScrollView.FOCUS_UP);
        loadMoview(arrayCategory[arrayCategoryIndex]);
    }

    public class MovieDiscoveryTask extends AsyncTask<CategoryBean, Void, CategoryBean> {

        private final String LOG_TAG = MovieDiscoveryTask.class.getSimpleName();

        @Override
        protected CategoryBean doInBackground(CategoryBean... params) {

            final String URL_BASE = getResources().getString(R.string.movie_api_base_discovery_url);
            final String API_KEY_PARAM = getResources().getString(R.string.movie_api_key_param);
            final String API_KEY_VALUE = getResources().getString(R.string.movie_api_key_value);
            final String SORT_PARAM = getResources().getString(R.string.movie_api_sort_param);
            final String PAGE = getResources().getString(R.string.movie_api_page_param);
            final String LANGUAGE_PARAM = getResources().getString(R.string.movie_api_language_param);
            final String LANGUAGE_VALUE = getResources().getString(R.string.movie_api_language_value);
            final String GEN_RES_PARAM = getResources().getString(R.string.movie_api_genres_param);
            CategoryBean category = params[0];
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

            Log.i(LOG_TAG, "CATEGORY: " + category.getTitulo().toUpperCase());
            Log.i(LOG_TAG, "url: " + uri.toString());
            String jsonResult = getJsonResultURL(uri.toString());
            return loadMoviesFromDiscoveryJson(jsonResult, category);
        }

        @Override
        protected void onPostExecute(CategoryBean category) {
            Log.i(LOG_TAG, "moviesList: " + category.getMovies().size());
            Log.i(LOG_TAG, "moviesListTemp: " + category.getMoviesTemp().size());
            Log.i(LOG_TAG, "real and current: " + category.getRealPage() + ", " + category.getCurrentPage());
            if(arrayCategoryIndex < arrayCategory.length){
                loadMoview(arrayCategory[arrayCategoryIndex]);
                arrayCategoryIndex++;
            }
            category.validateLoadMovies();
        }
    }

    private CategoryBean loadMoviesFromDiscoveryJson(String json, CategoryBean category) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray("results");
            StringBuilder sb = new StringBuilder();
            sb.append(" IDs: ");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jObj = array.getJSONObject(i);
                String path = jObj.getString("poster_path");
                if (path != null && !path.equals("null")) {
                    int id = jObj.getInt("id");
                    String title = jObj.getString("original_title");
                    Double voteAverage = jObj.getDouble("vote_average");
                    Float rating = voteAverage == null ? 0.0f : voteAverage.floatValue();
                    String synopsis = jObj.getString("overview");
                    String releaseDate = jObj.getString("release_date");
                    sb.append(id + ", ");
                    String pathUrl = path;
                    category.addMovie(new MovieBean(id, pathUrl, title, synopsis, rating, releaseDate));
                }
            }
            Log.i(LOG_TAG, sb.toString());
            return category;
        } catch (JSONException e) {
            Log.i(LOG_TAG, "Error al cargar las videos");
            return null;
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
            return getJsonResultURL(uri.toString());
        }

        @Override
        protected void onPostExecute(String jsonResult) {
            try{
                JSONObject jsonObject = new JSONObject(jsonResult);
                JSONArray array = jsonObject.getJSONArray("genres");
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    String name = jObj.getString("name");
                    Integer id = jObj.getInt("id");
                    map.put(name, id);
                }
                loadCategorys(map);
            } catch (JSONException e){
                Log.i(LOG_TAG, "Error al cargar los generos");
            }
        }
    }

    private String getJsonResultURL(String myUrl) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try {
            URL url = new URL(myUrl);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonStr;
    }

    private View getViewForCategory(ViewGroup viewGroup, final CategoryBean category, int index){
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_grid_item, null);

        TextView text = (TextView)convertView.findViewById(R.id.titulo);
        text.setText(category.getTitulo());
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
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtras(movie.getBundle());
                startActivity(intent);
            }
        });

        Button button = (Button)convertView.findViewById(R.id.button);
        Typeface fontFace = Typeface.createFromAsset(getActivity().getAssets(), "font/lqdkdz_nospace.ttf");
        button.setTypeface(fontFace);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                category.setCurrentPage(category.getCurrentPage() + 1);
                category.validateLoadMovies();
            }
        });

        viewGroup.addView(convertView);
        return convertView;

    }

}
