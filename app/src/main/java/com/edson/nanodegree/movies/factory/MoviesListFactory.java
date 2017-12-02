package com.edson.nanodegree.movies.factory;

import android.content.Context;
import android.util.Log;

import com.edson.nanodegree.movies.app.R;
import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.bean.MoviesListBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by edson on 1/12/2017.
 */

public class MoviesListFactory {

    private static final String LOG_TAG = MoviesListFactory.class.getSimpleName();

    public static MoviesListBean createMoviesListCategories(final Context context, final String[] genresNames, final String[] genresValues, final Set<String> genresValuesSelected){
        return new MoviesListBean() {
            @Override
            public void init() {
                List<MoviesGroupBean> moviesGroupBeans = new ArrayList<>();

                // all genres
                moviesGroupBeans.add(new MoviesGroupBean(context, "", null, context.getResources().getInteger(R.integer.page_size_8)));

                // adding genres
                for (int i = 0; i < genresValues.length; i++) {
                    String name = genresNames[i];
                    String id = genresValues[i];
                    boolean isActive = genresValuesSelected.contains(id);

                    Log.i(LOG_TAG, " - name: " + name + ", id: " + id + ", isActive: " + isActive);

                    MoviesGroupBean moviesGroupBean = new MoviesGroupBean(context, name, Integer.parseInt(id), context.getResources().getInteger(R.integer.page_size_8));
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

        };
    }

    public static MoviesListBean createMoviesListSearch(final Context context){
        return new MoviesListBean() {
            @Override
            public void init() {
                List<MoviesGroupBean> moviesGroupBeans = new ArrayList<>();
                moviesGroupBeans.add(new MoviesGroupBean(context, "Search", null, context.getResources().getInteger(R.integer.page_size_12)));

                this.setMoviesGroupBeans(moviesGroupBeans);
            }
        };
    }

    public static MoviesListBean createMoviesListFavorites(final Context context){
        return new MoviesListBean() {
            @Override
            public void init() {
                List<MoviesGroupBean> moviesGroupBeans = new ArrayList<>();
                moviesGroupBeans.add(new MoviesGroupBean(context, "Favorites", null, context.getResources().getInteger(R.integer.page_size_12)));

                this.setMoviesGroupBeans(moviesGroupBeans);
            }
        };
    }

}
