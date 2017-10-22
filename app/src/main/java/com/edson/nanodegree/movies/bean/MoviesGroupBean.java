package com.edson.nanodegree.movies.bean;

import android.content.Context;
import android.net.Uri;

import com.edson.nanodegree.movies.adapter.CustomGridViewAdapter;
import com.edson.nanodegree.movies.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edson on 10/09/2015.
 */
public class MoviesGroupBean {

    private String groupName;
    private String title;
    private List<MovieBean> movies;
    private List<MovieBean> moviesTemp;
    private int remotePage;
    private int pageSize;
    private int currentPage;
    private Integer id;
    private boolean isActive;

    private Uri.Builder uriBuilder;
    private String pageParameter;

    private CustomGridViewAdapter adapter;

    public MoviesGroupBean(Context context, String groupName, Integer id) {
        this.groupName = groupName;
        movies = new ArrayList<>();
        moviesTemp = new ArrayList<>();
        remotePage = 0;
        currentPage = 1;
        adapter = new CustomGridViewAdapter(context, new ArrayList<MovieBean>());
        pageSize = context.getResources().getInteger(R.integer.page_size);
        this.id = id;
        isActive = true;
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
        if(getRealPage() < currentPage){
            int total = movies.size();
            while(!moviesTemp.isEmpty() && getRealPage() < currentPage){
                MovieBean movieBean = moviesTemp.remove(0);
                movies.add(movieBean);
                adapter.getMovies().add(movieBean);
            }
            if(total < movies.size()){
                notifyDataSetChanged();
            }
            if(getRealPage() < currentPage){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
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

    public CustomGridViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CustomGridViewAdapter adapter) {
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

    public Uri.Builder getUriBuilder() {
        return uriBuilder;
    }

    public void setUriBuilder(Uri.Builder uriBuilder) {
        this.uriBuilder = uriBuilder;
    }

    public String getPageParameter() {
        return pageParameter;
    }

    public void setPageParameter(String pageParameter) {
        this.pageParameter = pageParameter;
    }
}
