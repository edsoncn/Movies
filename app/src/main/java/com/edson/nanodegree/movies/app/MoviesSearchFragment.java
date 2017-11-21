package com.edson.nanodegree.movies.app;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.Animation;

import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MoviesListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edson on 24/10/2017.
 */

public class MoviesSearchFragment extends AbstractMoviesListFragment {

    private String searchTerm;
    private SearchView searchView;
    private MenuItem searchViewItem;

    @Override
    protected MoviesListBean initMoviesListBean() {
        return new MoviesListBean() {
            @Override
            public void init() {
                List<MoviesGroupBean> moviesGroupBeans = new ArrayList<>();
                // all genres
                moviesGroupBeans.add(new MoviesGroupBean(getActivity(), "Search", null));

                this.setMoviesGroupBeans(moviesGroupBeans);
            }

            @Override
            public void generateUrls() {

                final String URL_BASE = getResources().getString(R.string.movie_api_base_search_url);
                final String API_KEY_PARAM = getResources().getString(R.string.movie_api_key_param);
                final String API_KEY_VALUE = getResources().getString(R.string.movie_api_key_value);
                final String PAGE = getResources().getString(R.string.movie_api_page_param);
                final String LANGUAGE_PARAM = getResources().getString(R.string.movie_api_language_param);
                final String LANGUAGE_VALUE = getResources().getString(R.string.movie_api_language_value);
                final String QUERY_PARAM = getResources().getString(R.string.movie_api_query_param);

                for(MoviesGroupBean moviesGroupBean : getMoviesGroupBeans()){
                    Uri.Builder builder = Uri.parse(URL_BASE).buildUpon()
                            .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                            .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)
                            .appendQueryParameter(QUERY_PARAM, searchTerm);
                    moviesGroupBean.setUriBuilder(builder);
                    moviesGroupBean.setPageParameter(PAGE);
                }
            }

        };
    }

    @Override
    public void load(){
        if(searchTerm != null && !searchTerm.isEmpty()){
            super.load();
        }
    }

    @Override
    public void loadMoviesGroupBeansInit() {
        MoviesGroupBean searchMoviesBean = moviesListBean.getMoviesGroupBeans().get(0);
        searchMoviesBean.setTitle(searchMoviesBean.getGroupName() + " by '" + searchTerm + "'");
        floatingHeader.setText(moviesListBean.getMoviesGroupBeans().get(0).getTitle());

        super.loadMoviesGroupBeansInit();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        searchViewItem = menu.findItem(R.id.search_view);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchTerm = query.toLowerCase();
                reset();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getActivity().getSupportFragmentManager().popBackStack();
                return false;
            }
        });

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        MenuItemCompat.expandActionView(searchViewItem);
    }

}
