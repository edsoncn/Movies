package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.widget.ImageView.ScaleType.CENTER_CROP;


public class DetailActivity extends ActionBarActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new DetailActivityFragment())
                    .commit();
        }
        setTitle(getResources().getString(R.string.title_movie_detail));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailActivityFragment extends Fragment {

        private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

        public DetailActivityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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

            Picasso.with(getActivity()) //
                    .load(urlImage500 + movie.getPathUrl()) //
                    .placeholder(R.drawable.placeholder) //
                    .error(R.drawable.error) //
                    .fit() //
                    .tag(getActivity()) //
                    .into(poster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
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
