package com.lowcoupling.whatmovie;

import android.view.View;

import java.util.concurrent.ConcurrentHashMap;

public class DataViewHelper {
    private static DataViewHelper instance;
    private static ConcurrentHashMap<String,Object> entities;
    public ConcurrentHashMap<String, View> getDataView() {
        return dataView;
    }

    private ConcurrentHashMap<String,View> dataView;

    public static ConcurrentHashMap<String, Object> getEntities() {
        return entities;
    }

    private DataViewHelper(){
        dataView = new ConcurrentHashMap<String,View>();
        entities = new ConcurrentHashMap<String,Object>();
    }
    public static DataViewHelper getInstance(){
        if (instance==null){
            instance = new DataViewHelper();
        }
        return instance;
    }

}
