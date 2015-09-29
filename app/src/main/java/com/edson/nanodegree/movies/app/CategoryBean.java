package com.edson.nanodegree.movies.app;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by edson on 10/09/2015.
 */
public class CategoryBean {

    private String titulo;
    private List<MovieBean> movies;
    private List<MovieBean> moviesTemp;
    private int remotePage;
    private int pageSize;
    private int currentPage;
    private LoadMovies loadMovies;
    private Integer id;

    private CustomGridViewAdapter adapter;

    public CategoryBean(Context context, String titulo, LoadMovies loadMovies, Integer id) {
        this.titulo = titulo;
        movies = new ArrayList<>();
        moviesTemp = new ArrayList<>();
        remotePage = 0;
        currentPage = 1;
        adapter = new CustomGridViewAdapter(context, new ArrayList<MovieBean>());
        pageSize = context.getResources().getInteger(R.integer.page_size);
        this.loadMovies = loadMovies;
        this.id = id;
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
    public void validateLoadMovies(){
        if(getRealPage() < currentPage){
            while(!moviesTemp.isEmpty() && getRealPage() < currentPage){
                MovieBean movieBean = moviesTemp.remove(0);
                movies.add(movieBean);
                adapter.getMovies().add(movieBean);
            }
            if(getRealPage() < currentPage){
                loadMovies.loadMoview(this);
            }else{
                adapter.notifyDataSetChanged();
            }
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    public int getRealPage(){
        return movies.size() / pageSize;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public LoadMovies getLoadMovies() {
        return loadMovies;
    }

}
