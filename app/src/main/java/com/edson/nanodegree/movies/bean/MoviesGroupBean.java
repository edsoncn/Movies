package com.edson.nanodegree.movies.bean;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.edson.nanodegree.movies.adapter.CustomMoviesGridViewAdapter;
import com.edson.nanodegree.movies.task.MoviesGroupClientRestTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edson on 10/09/2015.
 */
public class MoviesGroupBean {

    private static final String LOG_TAG = MoviesGroupBean.class.getSimpleName();

    public static final int STATE_INIT = 0;
    public static final int STATE_LOADED = 1;
    public static final int STATE_RESET = 2;
    public static final int STATE_LOAD_FINISHED = 3;

    private String groupName;
    private String title;
    private List<MovieBean> movies;
    private List<MovieBean> moviesTemp;

    private int remotePage;
    private int remoteTotalPages;
    private int remoteTotalResults;

    private int pageSize;
    private int currentPage;
    private Integer id;
    private boolean isActive;

    private CustomMoviesGridViewAdapter adapter;
    private Button plusButton;

    private int state;
    private int prevTotalMovies;

    public MoviesGroupBean(Context context, String groupName, Integer id, int pageSize) {
        this.groupName = groupName;
        this.title = groupName;
        movies = new ArrayList<>();
        moviesTemp = new ArrayList<>();
        remotePage = 0;
        currentPage = 1;
        adapter = new CustomMoviesGridViewAdapter(context, new ArrayList<MovieBean>());
        this.pageSize = pageSize;
        this.id = id;
        isActive = true;
        state = STATE_INIT;
        prevTotalMovies = -1;
    }

    /**
     * For load a new sort     *
     * */
    public void reset(){
        remotePage = 0;
        currentPage = 1;
        movies.clear();
        moviesTemp.clear();
        adapter.getMovies().clear();
        state = STATE_RESET;
        prevTotalMovies = -1;
    }

    public void load(MoviesListBean moviesListBean){
        state = STATE_LOADED;
        new MoviesGroupClientRestTask(moviesListBean).execute(this);
    }

    /**
     * Add movie until the page is completed else add to temp list
     * */
    public void addMovie(MovieBean movie) {
        if (getRealPage() < currentPage) {
            movies.add(movie);
            adapter.getMovies().add(movie);
        } else {
            moviesTemp.add(movie);
        }
    }

    /**
     * Validate whether the new page is completed with the
     * temporary list but carry more movies
     * */
    public boolean validateLoadMoviesPageComplete(){
        boolean isComplete;
        if(getRealPage() < currentPage){
            while(!moviesTemp.isEmpty() && getRealPage() < currentPage){
                MovieBean movieBean = moviesTemp.remove(0);
                movies.add(movieBean);
                adapter.getMovies().add(movieBean);
            }
            Log.i(LOG_TAG, "Validate Page Complete : RemotePage = " + getRemotePage() + ", RemoteTotalPages = " + getRemoteTotalPages());
            if(getRemotePage() >= getRemoteTotalPages()){
                state = STATE_LOAD_FINISHED;
                isComplete = true;
            }else if(getRealPage() < currentPage){
                isComplete = false;
            }else{
                isComplete = true;
            }
        }else{
            Log.i(LOG_TAG, "Validate Page Complete : RemotePage = " + getRemotePage() + ", RemoteTotalPages = " + getRemoteTotalPages());
            if(getRemotePage() >= getRemoteTotalPages()){
                state = STATE_LOAD_FINISHED;
            }
            isComplete = true;
        }

        //Validate if movies was added in order to update adapter
        if(prevTotalMovies < movies.size()){
            notifyDataSetChanged();
        }
        prevTotalMovies = movies.size();

        if(state == STATE_LOAD_FINISHED) {
            plusButton.setVisibility(View.GONE);
        }else{
            plusButton.setVisibility(View.VISIBLE);
        }

        return isComplete;
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    public int getRealPage(){
        return movies.size() / pageSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MovieBean> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieBean> movies) {
        this.movies = movies;
    }

    public int getRemotePage() {
        return remotePage;
    }

    public void setRemotePage(int remotePage) {
        this.remotePage = remotePage;
    }

    public CustomMoviesGridViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CustomMoviesGridViewAdapter adapter) {
        this.adapter = adapter;
    }

    public List<MovieBean> getMoviesTemp() {
        return moviesTemp;
    }

    public void setMoviesTemp(List<MovieBean> moviesTemp) {
        this.moviesTemp = moviesTemp;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getState() {
        return state;
    }

    public Button getPlusButton() {
        return plusButton;
    }

    public void setPlusButton(Button plusButton) {
        this.plusButton = plusButton;
    }

    public int getRemoteTotalPages() {
        return remoteTotalPages;
    }

    public void setRemoteTotalPages(int remoteTotalPages) {
        this.remoteTotalPages = remoteTotalPages;
    }

    public int getRemoteTotalResults() {
        return remoteTotalResults;
    }

    public void setRemoteTotalResults(int remoteTotalResults) {
        this.remoteTotalResults = remoteTotalResults;
    }
}
