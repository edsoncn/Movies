package com.edson.nanodegree.movies.bean;

import android.content.Context;
import android.net.Uri;

import com.edson.nanodegree.movies.adapter.CustomGridViewAdapter;
import com.edson.nanodegree.movies.app.ILoadMovies;
import com.edson.nanodegree.movies.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edson on 10/09/2015.
 */
public class CategoryBean {

    private String categoryName;
    private String title;
    private List<MovieBean> movies;
    private List<MovieBean> moviesTemp;
    private int remotePage;
    private int pageSize;
    private int currentPage;
    private ILoadMovies iLoadMovies;
    private Integer id;
    private boolean isActive;
    private Uri uri;

    private CustomGridViewAdapter adapter;

    public CategoryBean(Context context, String categoryName, ILoadMovies iLoadMovies, Integer id) {
        this.categoryName = categoryName;
        movies = new ArrayList<>();
        moviesTemp = new ArrayList<>();
        remotePage = 0;
        currentPage = 1;
        adapter = new CustomGridViewAdapter(context, new ArrayList<MovieBean>());
        pageSize = context.getResources().getInteger(R.integer.page_size);
        this.iLoadMovies = iLoadMovies;
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
    public boolean validateLoadMovies(){
        if(getRealPage() < currentPage){
            while(!moviesTemp.isEmpty() && getRealPage() < currentPage){
                MovieBean movieBean = moviesTemp.remove(0);
                movies.add(movieBean);
                adapter.getMovies().add(movieBean);
            }
            if(getRealPage() < currentPage){
                iLoadMovies.loadCategoryMoviesBeanInChain(this);
                return false;
            }else{
                adapter.notifyDataSetChanged();
                return true;
            }
        }else{
            adapter.notifyDataSetChanged();
            return true;
        }
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

    public ILoadMovies getiLoadMovies() {
        return iLoadMovies;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
