package com.edson.nanodegree.movies.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;

/** An image view which always remains square with respect to its width. */
final public class SquaredImageView extends android.support.v7.widget.AppCompatImageView {

    private View content;
    private Integer heightPlus;

    public SquaredImageView(Context context) {
        super(context);
        content = null;
        heightPlus = 0;
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(super.getDrawable() != null) {
            int height = (int) (getMeasuredWidth() *
                    (((float) super.getDrawable().getIntrinsicHeight()) /
                            super.getDrawable().getIntrinsicWidth()));
            setMeasuredDimension(getMeasuredWidth(), height);
            if (content != null) {
                if (content instanceof TableRow) {
                    TableRow row = (TableRow) content;
                    row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, height + getHeightPlusInDP()));
                }
            }
        }
    }

    public void setContent(View content){
        this.content = content;
    }

    public void setHeightPlus(Integer heightPlus) {
        this.heightPlus = heightPlus;
    }

    public int getHeightPlusInDP(){
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int)(heightPlus*scale + 0.5f);
    }

}