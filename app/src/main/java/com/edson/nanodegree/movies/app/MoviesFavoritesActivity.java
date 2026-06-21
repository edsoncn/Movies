package com.edson.nanodegree.movies.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import java.util.Objects;

/**
 * Created by edson on 21/11/2017.
 */

public class MoviesFavoritesActivity extends AppCompatActivity {

    private final String LOG_TAG = MoviesFavoritesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_favorites);
        Toolbar toolbar = findViewById(R.id.toolbarMoviesFavorites);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

}
