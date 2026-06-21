package com.edson.nanodegree.movies.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.edson.nanodegree.movies.bean.MoviesListBean;
import com.edson.nanodegree.movies.bean.MoviesGroupBean;
import com.edson.nanodegree.movies.helper.CoroutineTask;

/**
 * Created by edson on 12/10/2017.
 */

public class MoviesGroupClientRestTask extends CoroutineTask<MoviesGroupBean, MoviesGroupBean> {

    private final String LOG_TAG = MoviesGroupClientRestTask.class.getSimpleName();

    private MoviesListBean moviesListBean;

    private Context context;

    public MoviesGroupClientRestTask(MoviesListBean moviesListBean, Context context, LifecycleOwner lifecycleOwner){
        super(lifecycleOwner);
        this.moviesListBean = moviesListBean;
        this.context = context;
    }

    @Override
    protected MoviesGroupBean doInBackground(MoviesGroupBean moviesGroupBean) {
        Log.i(LOG_TAG, "Load: " + moviesGroupBean.getTitle());

        try {
            moviesListBean.getLoadMovies().loadMovies(moviesGroupBean);
            return moviesGroupBean;
        }catch (Exception e){
            return null;
        }

    }

    @Override
    protected void onPostExecute(MoviesGroupBean moviesGroupBean) {
        if(moviesGroupBean != null) {
            Log.i(LOG_TAG, "Movies list size: " + moviesGroupBean.getMovies().size());
            Log.i(LOG_TAG, "Movies list temp size: " + moviesGroupBean.getMoviesTemp().size());
            Log.i(LOG_TAG, "Real and current: " + moviesGroupBean.getRealPage() + ", " + moviesGroupBean.getCurrentPage());

            if(moviesGroupBean.noResults()) {
                CharSequence text = "No results!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            if (moviesGroupBean.validateLoadMoviesPageComplete()) {
                moviesListBean.loadMoviesGroupBeansInChain(context);
            } else {
                moviesGroupBean.setRemotePage(moviesGroupBean.getRemotePage() + 1);
                moviesGroupBean.load(moviesListBean, context, getLifecycleOwner());
            }
        }
    }
}