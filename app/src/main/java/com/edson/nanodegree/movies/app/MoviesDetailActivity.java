package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.edson.nanodegree.movies.bean.MovieBean;
import com.squareup.picasso.Picasso;

import static android.widget.ImageView.ScaleType.CENTER_CROP;


public class MoviesDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MoviesDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMoviesDetail);
        setSupportActionBar(toolbar);
    }

    public static class MoviesDetailFragment extends Fragment {

        private final String LOG_TAG = MoviesDetailFragment.class.getSimpleName();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.layout_movies_detail, container, false);

            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();

            MovieBean movie = new MovieBean(bundle);
            Log.i(LOG_TAG, movie.toString());

            TableRow tableRow = (TableRow) rootView.findViewById(R.id.row);

            SquaredImageView poster = (SquaredImageView) rootView.findViewById(R.id.poster);
            poster.setScaleType(CENTER_CROP);
            //Set tableRow for resize height when image load
            poster.setContent(tableRow);
            poster.setHeightPlus(4); // aditional height,
            // table row height = (image height + aditional height)

            // URL for larges images
            String urlImage500 = getResources().getString(R.string.movie_api_base_image_url_500);

            Picasso.with(getActivity())
                    .load(urlImage500 + movie.getPathUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .fit()
                    .tag(getActivity())
                    .into(poster, new com.squareup.picasso.Callback() {
                        @Override public void onSuccess() {}
                        @Override public void onError() {}
                    });

            TextView title = (TextView) rootView.findViewById(R.id.title);
            title.setText(movie.getTitle());

            RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
            ratingBar.setRating(movie.getRating());

            TextView release = (TextView) rootView.findViewById(R.id.release);
            String releaseFormat =  movie.getReleaseFormat(
                    getResources().getString(R.string.app_detail_release_format8601),
                    getResources().getString(R.string.app_detail_release_format));
            release.setText(releaseFormat != null ? releaseFormat : getResources().getString(R.string.app_detail_release_none));

            TextView synopsis = (TextView) rootView.findViewById(R.id.synopsis);
            synopsis.setText(movie.getSynopsis());

            return rootView;
        }
    }

}