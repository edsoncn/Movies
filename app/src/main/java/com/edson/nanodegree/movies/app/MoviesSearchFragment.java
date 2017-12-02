package com.edson.nanodegree.movies.app;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MoviesListBean;
import com.edson.nanodegree.movies.factory.MoviesListFactory;
import com.edson.nanodegree.movies.service.LoadMoviesSearchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edson on 24/10/2017.
 */

public class MoviesSearchFragment extends AbstractMoviesListFragment {

    private SearchView searchView;
    private MenuItem searchViewItem;

    private LoadMoviesSearchService loadMovies;

    @Override
    protected MoviesListBean createMoviesListBean() {
        return MoviesListFactory.createMoviesListSearch(getContext());
    }

    @Override
    public void init() {
        super.init();

        loadMovies = new LoadMoviesSearchService(getContext());
        moviesListBean.setLoadMovies(loadMovies);
    }

    @Override
    public void load(){
        if(loadMovies.getSearchTerm() != null && !loadMovies.getSearchTerm().isEmpty()){
            super.load();
        }
    }

    @Override
    public void loadMoviesGroupBeansInit() {
        MoviesGroupBean searchMoviesBean = moviesListBean.getMoviesGroupBeans().get(0);
        searchMoviesBean.setTitle(searchMoviesBean.getGroupName() + " by '" + loadMovies.getSearchTerm() + "'");
        floatingHeader.setText(moviesListBean.getMoviesGroupBeans().get(0).getTitle());

        super.loadMoviesGroupBeansInit();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_movies_search, menu);

        // Associate searchable configuration with the SearchView
        searchViewItem = menu.findItem(R.id.search_view);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                loadMovies.setSearchTerm(query.toLowerCase().trim());
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
