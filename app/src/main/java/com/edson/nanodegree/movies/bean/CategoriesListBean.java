package com.edson.nanodegree.movies.bean;

import android.util.Log;

import com.edson.nanodegree.movies.app.MoviesFragment;
import com.edson.nanodegree.movies.service.CategoryMoviesClientRestTask;

import java.util.List;

/**
 * Created by edson on 21/09/2017.
 */

public class CategoriesListBean {

    private final String LOG_TAG = CategoriesListBean.class.getSimpleName();

    private List<CategoryBean> categoryBeans;
    private int index;

    public void loadCategoryBeanInit(){

    }

    public void loadCetegoryBeanInChain(){

        if(index < categoryBeans.size() - 1) {
            index++;

            CategoryBean categoryBean = categoryBeans.get(index);
            Log.i(LOG_TAG, "LOAD: " + categoryBean.getTitle());

            if(categoryBean.isActive()) {
                new CategoryMoviesClientRestTask(this).execute(categoryBean);
            }else{
                loadCetegoryBeanInChain();
            }
        }
    }

    public List<CategoryBean> getCategoryBeans() {
        return categoryBeans;
    }

    public void setCategoryBeans(List<CategoryBean> categoryBeans) {
        this.categoryBeans = categoryBeans;
    }

}
