package com.edson.nanodegree.movies.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by edson on 14/09/2015.
 */

@Entity(tableName = "movie")
public class MovieBean {

    @PrimaryKey
    private int id;

    private String pathUrl;
    private String title;
    private String synopsis;
    private Float rating;
    private String release;

    public MovieBean(int id){
        this.id = id;
    }

    @Ignore
    public MovieBean(int id, String pathUrl) {
        this.id = id;
        this.pathUrl = pathUrl;
    }

    @Ignore
    public MovieBean(int id, String pathUrl, String title, String synopsis, Float rating, String release) {
        this.id = id;
        this.pathUrl = pathUrl;
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.release = release;
    }

    @Ignore
    public MovieBean(Bundle bundle){
        this.id = bundle.getInt("id");
        this.pathUrl = bundle.getString("pathUrl");
        this.title = bundle.getString("title");
        this.synopsis = bundle.getString("synopsis");
        this.rating = bundle.getFloat("rating");
        this.release = bundle.getString("release");
    }

    public Bundle getBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("pathUrl", pathUrl);
        bundle.putString("title", title);
        bundle.putString("synopsis", synopsis);
        bundle.putFloat("rating", rating);
        bundle.putString("release", release);
        return bundle;
    }

    /**
     * Format the release date
     * */
    public String getReleaseFormat(String formatIn, String formatOut){
        SimpleDateFormat dateFormatIn = new SimpleDateFormat(formatIn);
        SimpleDateFormat dateFormatOut = new SimpleDateFormat(formatOut, Locale.ENGLISH);
        try {
            return dateFormatOut.format(dateFormatIn.parse(release));
        }catch (Exception e){
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public void setPathUrl(String pathUrl) {
        this.pathUrl = pathUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MovieBean{");
        sb.append("id=").append(id);
        sb.append(", pathUrl='").append(pathUrl).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", synopsis='").append(synopsis).append('\'');
        sb.append(", rating=").append(rating);
        sb.append(", release='").append(release).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
