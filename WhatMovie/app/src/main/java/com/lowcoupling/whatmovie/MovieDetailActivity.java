package com.lowcoupling.whatmovie;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lowcoupling.whatmovie.datamodel.Movie;
import com.lowcoupling.whatmovie.loaders.MovieDetailLoader;


public class MovieDetailActivity extends Activity  {
    private String imdbId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        if (savedInstanceState == null) {
            Fragment fg = new PlaceholderFragment();
            fg.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.container, fg)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks {

        private  Movie movie;
        private String imdbId;
        private View rootView;
        private ImageView iv;


        public PlaceholderFragment() {
        }

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            Log.d(this.getClass().getName(), "args " + args);
            Bundle b = args;
            if (b!=null) {
                imdbId = b.getString("movieDetailInput");
                movie = (Movie) DataViewHelper.getInstance().getEntities().get(imdbId);
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            this.getActivity().setTitle("");
            rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            iv = (ImageView) rootView.findViewById(R.id.detail_pic);
            if (this.getLoaderManager().getLoader(0) == null) {
                this.getLoaderManager().initLoader(0, null, this).forceLoad();
            } else {
                this.getLoaderManager().restartLoader(0, null, this).forceLoad();
            }
            return rootView;
        }


        @Override
        public Loader onCreateLoader(int i, Bundle bundle) {
            Loader loader = null;
            loader = new MovieDetailLoader(this.getActivity(), imdbId);
            return loader;
        }

        @Override
        public void onLoadFinished(Loader loader, Object o) {
            Cursor mc = (Cursor)o;
            int i = loader.getId();
            String imdbId = ((MovieDetailLoader) loader).getImdbId();
            //if the movie has been retrieved from the DB
            Movie movie = null;
            if (mc != null && mc.getCount() > 0) {

                mc.moveToFirst();
                movie = new Movie(mc);
            }

            if(movie!=null) {
                this.getActivity().setTitle(movie.getTitle());
                TextView textView =(TextView) rootView.findViewById(R.id.detail_text);
                textView.setText(movie.getOverview());
                TextView titleText =(TextView) rootView.findViewById(R.id.title_text);
                titleText.setText(movie.getTitle());
                TextView ratingText = (TextView) rootView.findViewById(R.id.rating_text);
                ratingText.setText("");
                if(movie.getRating()!=null){
                    String ratingString = "rated: "+movie.getRating()+" with "+movie.getRatinvVotes()+" votes";
                    ratingText.setText(ratingString);
                }
                if(movie.getThumb()!=null) {
                    iv.setImageDrawable(movie.getThumb());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }

    }
}
