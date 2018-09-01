package foadkhezri.github.com.earthquakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

 class EarthquakeAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Earthquake>> {

    /** Query URL */
    private String mUrl;

    public EarthquakeAsyncTaskLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {

        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        return QueryUtils.fetchEarthquakeData(mUrl);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

}
