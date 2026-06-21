package com.edson.nanodegree.movies.app;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MoviesListBean;
import com.edson.nanodegree.movies.factory.MoviesListFactory;
import com.edson.nanodegree.movies.service.LoadMoviesSearchService;

import java.util.Objects;

/**
 * Created by edson on 24/10/2017.
 */

public class MoviesSearchFragment extends AbstractMoviesListFragment {

    private SearchView searchView;
    private MenuItem searchViewItem;

    private LoadMoviesSearchService loadMovies;

    @Override
    protected MoviesListBean createMoviesListBean() {
        return MoviesListFactory.createMoviesListSearch(getContext(), getViewLifecycleOwner());
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
    public void loadMoviesGroupBeansInit(Context context) {
        MoviesGroupBean searchMoviesBean = moviesListBean.getMoviesGroupBeans().get(0);
        searchMoviesBean.setTitle(searchMoviesBean.getGroupName() + " by '" + loadMovies.getSearchTerm() + "'");
        floatingHeader.setText(moviesListBean.getMoviesGroupBeans().get(0).getTitle());

        super.loadMoviesGroupBeansInit(getContext());

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

        searchView.setOnCloseListener(() -> {
            requireActivity().getSupportFragmentManager().popBackStack();
            return false;
        });

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (searchView != null) {
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
        }

        if (searchViewItem != null) {
            // MenuItemCompat.expandActionView is deprecated
            // Use the MenuItem's native expandActionView() method instead
            searchViewItem.expandActionView();
        }
    }

}
