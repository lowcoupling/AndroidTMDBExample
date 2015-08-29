package com.lowcoupling.whatmovie;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.lowcoupling.whatmovie.datamodel.Movie;
import com.lowcoupling.whatmovie.intentServices.MovieRatingIntentService;
import com.lowcoupling.whatmovie.intentServices.MovieThumbIntentService;
import com.lowcoupling.whatmovie.loaders.MovieDetailLoader;
import com.lowcoupling.whatmovie.loaders.PopMoviesLoader;
import com.lowcoupling.whatmovie.updaters.MovieDetailUpdater;
import com.lowcoupling.whatmovie.intentServices.PopMoviesIntentService;
import com.lowcoupling.whatmovie.updaters.MovieRatingUpdater;
import com.lowcoupling.whatmovie.updaters.MovieThumbUpdater;
import com.lowcoupling.whatmovie.intentServices.MovieDetailIntentService;
import com.lowcoupling.whatmovie.updaters.PopMoviesUpdater;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public  class PopMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks,AbsListView.OnScrollListener {
    private MovieThumbUpdater mtu;
    private int loadingCounter=1;
    public final String DEBUG_TAG = "debug";
    private ArrayList<Movie> movieList;
    private MovieArrayAdapter adapter;
    protected Queue<String> moviesToSearch;
    private int currentScrollState;
    private int currentVisibleItemCount;
    private int totalItemCount;
    private int lastVisibleItem;
    private int load;
    private PopMoviesUpdater pmu;
    private MovieDetailUpdater updater;
    private MovieRatingUpdater mru;
    private int currentFirstVisibleItem;
    private boolean networkWasNotAvailable;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    private boolean isLoading;
    private int currentPage;
    public PopMoviesFragment() {
        moviesToSearch = new ConcurrentLinkedQueue<String>();
        currentPage=1;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.d("PopMoviesFragment","saving context");
        int movieSize = movieList.size();
        int page = 0;
        Iterator<Movie> mit = movieList.iterator();
        String [] movieStringArray = new String[movieSize];
        int pos =0;
        while(mit.hasNext()){
            Movie tm = mit.next();
            movieStringArray[pos] = tm.getImdbId();
            pos++;
        }
        outState.putStringArray("movies",movieStringArray);
        outState.putInt("currentPage", page);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieList = new ArrayList<Movie>();
        MovieArrayAdapter adapter = new MovieArrayAdapter(getActivity(), movieList);
        this.adapter=adapter;
        if (savedInstanceState != null) {
            String [] moviesId = savedInstanceState.getStringArray("movies");
            int page = savedInstanceState.getInt("currentPage");
            for (int i=0; i<moviesId.length;i++){
                Movie tm =(Movie)DataViewHelper.getEntities().get(moviesId[i]);
                if(tm!=null) {
                    adapter.add(tm);
                }
            }
            currentPage = page;
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updater = new MovieDetailUpdater();
        this.getActivity().registerReceiver(updater,new IntentFilter("MovieDetailUpdate"));
        pmu = new PopMoviesUpdater(adapter,this);
        this.getActivity().registerReceiver(pmu,new IntentFilter("PopMoviesUpdate"));
        mtu = new MovieThumbUpdater();
        this.getActivity().registerReceiver(mtu,new IntentFilter("MovieThumbUpdate"));
        mru = new MovieRatingUpdater();
        this.getActivity().registerReceiver(mru, new IntentFilter("MovieRatingUpdate"));


    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            this.getActivity().unregisterReceiver(mtu);
            this.getActivity().unregisterReceiver(pmu);
            this.getActivity().unregisterReceiver(updater);
            this.getActivity().unregisterReceiver(mru);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(this.adapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Intent intent = new Intent(PopMoviesFragment.this.getActivity(), MovieDetailActivity.class);
                Bundle b = new Bundle();
                b.putString("movieDetailInput", movieList.get(position).getImdbId()); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                intent.putExtra("page", currentPage);
                startActivity(intent);
                //final String item = (String) parent.getItemAtPosition(position);

            }


        });
        if(adapter.values.size()==0) {
            if (isNetworkAvailable()) {
                if (networkWasNotAvailable) {
                    adapter.clear();
                }
                Intent msgIntent = new Intent(this.getActivity(), PopMoviesIntentService.class);
                this.getActivity().startService(msgIntent);
                networkWasNotAvailable = false;
            } else {
                if (!networkWasNotAvailable) {
                    adapter.clear();
                }
                networkWasNotAvailable = true;
                if (PopMoviesFragment.this.getLoaderManager().getLoader(0) == null) {
                    PopMoviesFragment.this.getLoaderManager().initLoader(0, null, PopMoviesFragment.this).forceLoad();
                } else {
                    PopMoviesFragment.this.getLoaderManager().restartLoader(0, null, PopMoviesFragment.this).forceLoad();
                }
            }
        }
        
        return rootView;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.currentFirstVisibleItem = firstVisibleItem;
        this.currentVisibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
        this.lastVisibleItem = firstVisibleItem+visibleItemCount;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.currentScrollState = scrollState;
        this.isScrollCompleted();
    }

    public void loadMoreData(){
        if(isNetworkAvailable()) {
            if (networkWasNotAvailable){
                adapter.clear();
                currentPage = 0;

            }else {
                currentPage++;
            }
            networkWasNotAvailable = false;
            Intent msgIntent = new Intent(this.getActivity(), PopMoviesIntentService.class);
            msgIntent.putExtra("name", "pop movies intent");
            msgIntent.putExtra("page", currentPage);
            this.getActivity().startService(msgIntent);
        }else{
            if (!networkWasNotAvailable){
                adapter.clear();
                currentPage = 0;
                if(PopMoviesFragment.this.getLoaderManager().getLoader(0)==null) {
                    PopMoviesFragment.this.getLoaderManager().initLoader(0, null, PopMoviesFragment.this).forceLoad();
                }else{
                    PopMoviesFragment.this.getLoaderManager().restartLoader(0, null, PopMoviesFragment.this).forceLoad();
                }
            }else {
                currentPage++;
            }
            networkWasNotAvailable = true;

        }


    }
    private void isScrollCompleted() {
        //Log.d(this.getClass().getName(),lastVisibleItem+" "+totalItemCount);
        if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE
            && (this.totalItemCount == this.lastVisibleItem)
                ) {
            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work for load more date! ***/
            //if(!isLoading){
                //Log.d(this.getClass().getName(),"SCROLL COMPLETED PAGE "+currentPage);
                isLoading = true;
                loadMoreData();
            //}
        }
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        Loader loader = null;
        if(i!=0) {
            String imdbId = bundle.getString("imdbId");
            loader = new MovieDetailLoader(getActivity(), imdbId);
        }else{
            loader = new PopMoviesLoader(getActivity());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        Cursor mc = (Cursor)o;
        int i = loader.getId();
        if (i==0){
            mc.moveToFirst();
            while(!mc.isLast()){
                Movie movie = new Movie();
                movie.initLightWeight(mc);
                movie.setThumb(null);
                //we need to keep track of what view is linked to each movie
                DataViewHelper.getInstance().getEntities().put(movie.getImdbId(), movie);
                this.adapter.add(movie);
                mc.moveToNext();
            }
            this.adapter.notifyDataSetChanged();
        }else {

            String imdbId = ((MovieDetailLoader)loader).getImdbId();
            //if the movie has been retrieved from the DB
            if (mc != null && mc.getCount() > 0) {

                mc.moveToFirst();
                Movie movie = new Movie(mc);
                //if the mv has been retrieved from the DB and has an image
                if (movie.getThumb() != null) {
                    //we get the latest view we know to be linked to the movie
                    View view = DataViewHelper.getInstance().getDataView().get(movie.getImdbId());
                    //from the view we get the viewholder
                    ViewHolder vh = (ViewHolder)view.getTag();
                    ImageView imageView = null;
                    //we check if it already has an imageview (So we do not have to create one)
                    if(vh!=null && vh.imageView!=null){
                        imageView = vh.imageView;
                    }else{
                       //if the imageView is null we create a brand new one
                       imageView=  (ImageView) view.findViewById(R.id.icon);
                    }
                    // when the viewholder refers to a different imageUrl (movie) it is updated
                    if (vh!=null &&
                            vh.imageUrl!=null&&
                            movie.getThumbUrl()!=null&&
                            vh.imageUrl.equals(movie.getThumbUrl())
                            ){
                    }else {
                        //otherwise nothing is done. This to prevent flickering in the list view
                        imageView.setImageDrawable(movie.getThumb());
                        TextView ovText = (TextView) view.findViewById(R.id.overview);

                    }

                } else {

                    //if the mn has been retrieved from the DB, it doesn't have an image and
                    //doesn't have an url to an image
                    if (movie.getThumbUrl() == null) {
                        //we need to retrieve the movie detail through the related
                        //intent service
                        Intent detailIntent = new Intent(PopMoviesFragment.this.getActivity(), MovieDetailIntentService.class);
                        detailIntent.setAction("Detail" + movie.getImdbId());
                        detailIntent.putExtra("imdbId", movie.getImdbId());
                        detailIntent.putExtra("traktKey", PopMoviesFragment.this.getActivity().getString(R.string.api_key));
                        detailIntent.putExtra("function", PopMoviesFragment.this.getActivity().getString(R.string.searchFunction));
                        detailIntent.putExtra("authority", PopMoviesFragment.this.getActivity().getString(R.string.authority));
                        PopMoviesFragment.this.getActivity().startService(detailIntent);

                    } else {

                        //the mv has been retrieved from the DB, it doesn't have an image BUT
                        //it has an URL
                        //Log.d("MovieDetailLoader", "retrieved from DB " + movie.getImdbId() + " " + movie.getThumbUrl());
                        //we already have the movie details
                        View view = DataViewHelper.getInstance().getDataView().get(movie.getImdbId());
                        //we need to retrieve the thumb
                        if (view != null) {
                            ViewHolder vh = (ViewHolder) view.getTag();
                            if (vh!=null){
                                vh.imageUrl = movie.getThumbUrl();
                                view.setTag(vh);
                            }
                            Intent msgIntent = new Intent(this.getActivity(), MovieThumbIntentService.class);
                            msgIntent.putExtra("imdbId", movie.getImdbId());
                            msgIntent.putExtra("thumbUrl", movie.getThumbUrl());
                            this.getActivity().startService(msgIntent);
                        }

                    }
                }
            //we didn't manage to retrieve the mv from the db
            } else {
                Intent detailIntent = new Intent(PopMoviesFragment.this.getActivity(), MovieDetailIntentService.class);
                detailIntent.setAction("Detail" + imdbId);
                detailIntent.putExtra("imdbId", imdbId);
                detailIntent.putExtra("traktKey", PopMoviesFragment.this.getActivity().getString(R.string.api_key));
                detailIntent.putExtra("function", PopMoviesFragment.this.getActivity().getString(R.string.searchFunction));
                detailIntent.putExtra("authority", PopMoviesFragment.this.getActivity().getString(R.string.authority));
                PopMoviesFragment.this.getActivity().startService(detailIntent);
            }

            Intent ratingIntent = new Intent(this.getActivity(), MovieRatingIntentService.class);
            ratingIntent.setAction("Rating" + imdbId);
            ratingIntent.putExtra("imdbId", imdbId);
            ratingIntent.putExtra("traktKey", this.getActivity().getString(R.string.api_key));
            ratingIntent.putExtra("authority", this.getActivity().getString(R.string.authority));
            getActivity().startService(ratingIntent);
        }

        mc.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    //it is the adapter that retrieves data from the DB
    public class MovieArrayAdapter extends ArrayAdapter<Movie> {
        private final Context context;
        List<Movie> values;
        public MovieArrayAdapter(Context context, List<Movie> objects) {
            super(context, R.layout.movie_list_item, objects);
            this.context = context;
            values = objects;

        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        //Adapter providing a view for the item
        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Movie movie =  (Movie)values.get(position);
            ViewHolder tag = null;
            if (rowView!=null) {
                tag = (ViewHolder) rowView.getTag();
            }
            movie = (Movie) DataViewHelper.getInstance().getEntities().get(movie.getImdbId());
            if (tag!=null && tag.imdbId.equals(movie.getImdbId())){

            }else {
                if (rowView ==null) {
                    rowView = inflater.inflate(R.layout.movie_list_item, parent, false);
                }

                if (tag==null){
                    tag = new ViewHolder();
                    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
                    tag.imageView = imageView;
                    rowView.setTag(tag);

                }
                tag.imdbId = movie.getImdbId();
                tag.imageUrl="";
                tag.imageView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                DataViewHelper.getInstance().getDataView().put(movie.getImdbId(), rowView);
                Bundle bd = new Bundle();
                bd.putString("imdbId", movie.getImdbId());
                if (PopMoviesFragment.this.getLoaderManager().getLoader(loadingCounter) == null) {
                    PopMoviesFragment.this.getLoaderManager().initLoader(loadingCounter, bd, PopMoviesFragment.this).forceLoad();
                } else {
                    PopMoviesFragment.this.getLoaderManager().restartLoader(loadingCounter, bd, PopMoviesFragment.this).forceLoad();
                }
                loadingCounter++;
            }
            return rowView;
        }


    }

}

