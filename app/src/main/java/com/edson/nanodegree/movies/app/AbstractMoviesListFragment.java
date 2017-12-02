package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
        Log.i(LOG_TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.layout_movies_main, container, false);

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

        init();
        load();

        return rootView;
    }

    public void init(){
        moviesListBean = createMoviesListBean();
        moviesListBean.init();

        initViews();
    }

    public void load(){
        loadMoviesGroupBeansInit();
    }

    protected void initViews(){
        // load a layoutContent for each category
        categoryViews = new LinearLayout[moviesListBean.getMoviesGroupBeans().size()];
        for (int i = 0; i < moviesListBean.getMoviesGroupBeans().size(); i++) {
            categoryViews[i] = getViewForMoviesGroup(layoutContent, moviesListBean.getMoviesGroupBeans().get(i), i);
        }

        // load title for the header with the first group
        floatingHeader.setText(moviesListBean.getMoviesGroupBeans().get(0).getTitle());

        // load title for each others group
        for(int i = 1; i < categoryViews.length; i++){
            TextView text = (TextView) categoryViews[i].findViewById(R.id.title);
            text.setText(moviesListBean.getMoviesGroupBeans().get(i).getTitle());
        }

    }

    protected abstract MoviesListBean createMoviesListBean();

    public void loadMoviesGroupBeansInit() {
        scrollIndex = -1;
        moviesScroll.fullScroll(ScrollView.FOCUS_UP);

        moviesListBean.loadMoviesGroupBeansInit();
    }

    public void reset(){
        moviesListBean.reset();
        resetViews();

        load();
    }

    protected void resetViews(){
        for(int i = 1; i < moviesListBean.getMoviesGroupBeans().size(); i++) {
            categoryViews[i].setActivated(moviesListBean.getMoviesGroupBeans().get(i).isActive());
            categoryViews[i].setVisibility(moviesListBean.getMoviesGroupBeans().get(i).isActive() ? View.VISIBLE : View.GONE);
        }
    }

    protected View getViewForMoviesGroup(ViewGroup viewGroup, final MoviesGroupBean moviesGroupBean, int index){
        View convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_movies_grid_section, null);

        convertView.setActivated(moviesGroupBean.isActive());
        convertView.setVisibility(moviesGroupBean.isActive() ? View.VISIBLE : View.GONE);

        TextView text = (TextView)convertView.findViewById(R.id.title);
        text.setText(moviesGroupBean.getTitle());
        if(index == 0){
            text.setHeight(0);
        }

        MoviesGridView grid = (MoviesGridView)convertView.findViewById(R.id.grid);
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

        Button plusButton = (Button)convertView.findViewById(R.id.plusButton);
        Typeface fontFace = Typeface.createFromAsset(getActivity().getAssets(), getResources().getString(R.string.font_lqdkdz_nospace));
        plusButton.setTypeface(fontFace);
        plusButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(moviesGroupBean.getState() == MoviesGroupBean.STATE_LOADED){
                    moviesGroupBean.setCurrentPage(moviesGroupBean.getCurrentPage() + 1);
                    if(!moviesGroupBean.validateLoadMoviesPageComplete()){
                        moviesGroupBean.setRemotePage(moviesGroupBean.getRemotePage() + 1);
                        moviesGroupBean.load(moviesListBean);
                    }
                }
            }
        });
        plusButton.setVisibility(View.GONE);
        moviesGroupBean.setPlusButton(plusButton);

        viewGroup.addView(convertView);
        return convertView;

    }

}
