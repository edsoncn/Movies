package com.edson.nanodegree.movies.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

/**
 * Created by edson on 20/09/2015.
 */
public class CustomGridViewAdapter extends ArrayAdapter<MovieBean> {

    private ArrayList<MovieBean> movies;
    private final Context context;
    private String urlImage;

    public CustomGridViewAdapter(Context context, ArrayList<MovieBean> movies){
        super(context, 0, movies);
        this.movies = movies;
        this.context = context;

        //Images for grid
        urlImage = context.getResources().getString(R.string.movie_api_base_image_url);
    }

    public ArrayList<MovieBean> getMovies() {
        return movies;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        final MovieBean movieBean = getItem(position);
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(urlImage + movieBean.getPathUrl()) //
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.error) //
                .fit() //
                .tag(context) //
                .into(view, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError() {
                    }
                });

        return view;
    }
}
