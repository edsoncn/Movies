package com.edson.nanodegree.movies.helper;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A safe AsyncTask replacement for Java that is Lifecycle-aware.
 * This avoids the "CoroutinesInternalError" by using standard Java Concurrency.
 */
public abstract class CoroutineTask<U, T> {

    // Shared thread pool for background work (similar to AsyncTask.THREAD_POOL_EXECUTOR)
    private static final ExecutorService sExecutor = Executors.newFixedThreadPool(4);
    private final LifecycleOwner lifecycleOwner;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isCancelled = false;

    public CoroutineTask(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;

        // Automatically cancel if the Fragment/Activity is destroyed
        lifecycleOwner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(LifecycleOwner owner) {
                isCancelled = true;
            }
        });
    }

    protected abstract T doInBackground(U u);

    protected abstract void onPostExecute(T result);

    public void execute(final U u) {
        sExecutor.execute(() -> {
            // 1. Run background work
            final T result = doInBackground(u);

            // 2. Switch back to Main Thread for UI
            mainHandler.post(() -> {
                // 3. Only update UI if the lifecycle is still active
                if (!isCancelled && lifecycleOwner.getLifecycle().getCurrentState().isAtLeast(androidx.lifecycle.Lifecycle.State.INITIALIZED)) {
                    onPostExecute(result);
                }
            });
        });
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

}