package com.edson.nanodegree.movies.app;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by edson on 13/09/2015.
 */
public class MoviesGridView extends GridView {

    public MoviesGridView(Context context) {
        super(context);
    }

    public MoviesGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoviesGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }
        else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
