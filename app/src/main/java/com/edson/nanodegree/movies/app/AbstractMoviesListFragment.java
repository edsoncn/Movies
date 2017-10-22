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

public abstract class AbstractMoviesListFragment extends Fragment {

    private static final String LOG_TAG = AbstractMoviesListFragment.class.getSimpleName();

    protected MoviesListBean moviesListBean;

    protected LinearLayout layoutContent;
    protected ScrollView moviesScroll;
    protected int scrollIndex;
    protected View[] categoryViews;
    protected TextView floatingHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_movies, container, false);

        layoutContent = (LinearLayout) rootView.findViewById(R.id.movies_layout);
        moviesScroll = (ScrollView) rootView.findViewById(R.id.movies_scroll);
        floatingHeader = (TextView) rootView.findViewById(R.id.floating_header);

        this.setHasOptionsMenu(true);

        moviesScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                if(categoryViews != null){
                    int i = 0;
                    for (View v : categoryViews) {
                        MoviesGroupBean moviesGroupBeans = moviesListBean.getMoviesGroupBeans().get(i);
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

        moviesListBean = initMoviesListBean();
        moviesListBean.init();

        initViews();
        loadMoviesGroupBeansInit();

        return rootView;
    }

    protected abstract MoviesListBean initMoviesListBean();

    protected void initViews(){
        // load a layoutContent for each category
        categoryViews = new LinearLayout[moviesListBean.getMoviesGroupBeans().size()];
        for (int i = 0; i < moviesListBean.getMoviesGroupBeans().size(); i++) {
            categoryViews[i] = getViewForMoviesGroup(layoutContent, moviesListBean.getMoviesGroupBeans().get(i), i);
        }

        // load title for each group
        floatingHeader.setText(moviesListBean.getMoviesGroupBeans().get(0).getTitle());

        for(int i = 1; i < categoryViews.length; i++){
            TextView text = (TextView) categoryViews[i].findViewById(R.id.title);
            text.setText(moviesListBean.getMoviesGroupBeans().get(i).getTitle());
        }

    }

    public void loadMoviesGroupBeansInit() {
        scrollIndex = -1;
        moviesScroll.fullScroll(ScrollView.FOCUS_UP);

        moviesListBean.loadMoviesGroupBeansInit();
    }

    public void reset(){
        moviesListBean.reset();
        resetViews();
        loadMoviesGroupBeansInit();
    }

    protected void resetViews(){
        for(int i = 1; i < moviesListBean.getMoviesGroupBeans().size(); i++) {
            categoryViews[i].setActivated(moviesListBean.getMoviesGroupBeans().get(i).isActive());
            categoryViews[i].setVisibility(moviesListBean.getMoviesGroupBeans().get(i).isActive() ? View.VISIBLE : View.GONE);
        }
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
                    moviesListBean.callMoviesListClientRestTask(moviesGroupBean);
                }
            }
        });

        viewGroup.addView(convertView);
        return convertView;

    }

}
