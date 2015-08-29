package com.lowcoupling.whatmovie;

import android.widget.ImageView;

/**
 * Created by LowCoupling on 05/08/15.
 */
// here is a viewholder used to store data about the loaded item in the listview.
// when the viewholder refers to a different imageUrl it is refreshed
public class ViewHolder {
    public ImageView imageView;
    public String imageUrl;
    public String imdbId;
}
