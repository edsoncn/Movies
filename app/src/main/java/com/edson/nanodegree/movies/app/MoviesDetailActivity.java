package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.edson.nanodegree.movies.bean.MovieBean;
import com.edson.nanodegree.movies.helper.AppDatabase;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.widget.ImageView.ScaleType.CENTER_CROP;


public class MoviesDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MoviesDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMoviesDetail);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static class MoviesDetailFragment extends Fragment {

        private final String LOG_TAG = MoviesDetailFragment.class.getSimpleName();

        private View rootView;

        private MovieBean movie;
        private AppDatabase moviesDB;

        private FloatingActionButton addFavorite;
        private MenuItem actionRemoveMenuItem;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.layout_movies_detail, container, false);

            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();

            movie = new MovieBean(bundle);
            moviesDB = AppDatabase.getMoviesDBSingleton(getActivity().getApplicationContext());
            Log.i(LOG_TAG, movie.toString());

            this.setHasOptionsMenu(true);

            final TableRow headRow = (TableRow) rootView.findViewById(R.id.head_row);

            final LinearLayout synopsisLayout = (LinearLayout) rootView.findViewById(R.id.synopsis_layout);

            final AtomicBoolean posterLoaded = new AtomicBoolean();

            final SquaredImageView poster = (SquaredImageView) rootView.findViewById(R.id.poster);
            poster.setScaleType(CENTER_CROP);
            //Set tableRow for resize height when image load
            poster.setContent(headRow);
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
                        @Override public void onSuccess() {
                            posterLoaded.set(true);
                        }
                        @Override public void onError() {
                        }
                    });

            final SquaredImageView posterFull = (SquaredImageView) rootView.findViewById(R.id.poster_full);
            posterFull.setScaleType(CENTER_CROP);
            posterFull.setHeightPlus(4);
            posterFull.setVisibility(View.GONE);

            poster.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (posterLoaded.get()) {
                        headRow.setVisibility(View.GONE);
                        synopsisLayout.setVisibility(View.GONE);
                        if (posterFull.getDrawable() == null) {
                            posterFull.setImageDrawable(poster.getDrawable());
                        }
                        posterFull.setVisibility(View.VISIBLE);
                    }
                }
            });

            posterFull.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    posterFull.setVisibility(View.GONE);
                    headRow.setVisibility(View.VISIBLE);
                    synopsisLayout.setVisibility(View.VISIBLE);
                }
            });

            TextView title = (TextView) rootView.findViewById(R.id.title);
            title.setText(movie.getTitle());

            RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
            ratingBar.setRating(movie.getRating() / 2);
            Log.i(LOG_TAG, movie.getTitle() + ": " + ratingBar.getRating() + ", " + movie.getRating());

            TextView release = (TextView) rootView.findViewById(R.id.release);
            String releaseFormat = movie.getReleaseFormat(
                    getResources().getString(R.string.app_detail_release_format8601),
                    getResources().getString(R.string.app_detail_release_format));
            release.setText(releaseFormat != null ? releaseFormat : getResources().getString(R.string.app_detail_release_none));

            TextView synopsis = (TextView) rootView.findViewById(R.id.synopsis_detail);
            synopsis.setText(movie.getSynopsis());

            addFavorite = (FloatingActionButton) rootView.findViewById(R.id.addFavorite);
            addFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override protected Boolean doInBackground(Void... params) {
                            try {
                                moviesDB.movieDao().insertAll(movie);
                                return true;
                            } catch (Exception e) {
                                Log.i(LOG_TAG, "There was an error trying to insert the movie to favorites: " + e.getMessage());
                                return false;
                            }
                        }
                        @Override protected void onPostExecute(Boolean inserted) {
                            if (inserted) {
                                Snackbar.make(view, "The movie was added to your favorites", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                addFavorite.setVisibility(View.GONE);
                                actionRemoveMenuItem.setEnabled(true);
                            } else {
                                Snackbar.make(view, "An error occur try it later", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                    }.execute();
                }
            });

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.clear();
            inflater.inflate(R.menu.menu_movies_detail, menu);

            actionRemoveMenuItem = menu.findItem(R.id.action_remove);
            actionRemoveMenuItem.setEnabled(false);

            validateFavoriteMovie();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            //noinspection SimplifiableIfStatement
            Log.i(LOG_TAG, "ItemId:" + id);

            if (id == R.id.action_remove){
                new AsyncTask<Void, Void, Boolean>() {
                    @Override protected Boolean doInBackground(Void... params) {
                        try {
                            moviesDB.movieDao().delete(movie);
                            return true;
                        } catch (Exception e) {
                            Log.i(LOG_TAG, "There was an error trying to remove the movie from favorites: " + e.getMessage());
                            return false;
                        }
                    }
                    @Override protected void onPostExecute(Boolean deleted) {
                        if (deleted) {
                            addFavorite.setVisibility(View.VISIBLE);
                            actionRemoveMenuItem.setEnabled(false);
                            Snackbar.make(rootView, "The movie was removed from favorites", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            Snackbar.make(rootView, "An error occur try it later", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }.execute();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void validateFavoriteMovie() {
            new AsyncTask<Void, Void, Boolean>() {
                @Override protected Boolean doInBackground(Void... params) {
                    return moviesDB.movieDao().findById(movie.getId()) != null;
                }
                @Override protected void onPostExecute(Boolean exists) {
                    if (!exists) addFavorite.setVisibility(View.VISIBLE);
                    else actionRemoveMenuItem.setEnabled(true);
                }
            }.execute();

        }

    }

}
