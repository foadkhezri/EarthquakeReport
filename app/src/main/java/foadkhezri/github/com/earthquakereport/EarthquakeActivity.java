package foadkhezri.github.com.earthquakereport;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class EarthquakeActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int EARTHQUAKE_LOADER_ID = 1;
    private static boolean isConnected = false;
    TextView mEmptyState;
    ArrayList<Earthquake> earthQuakes = new ArrayList<>();
    ProgressBar mProgressBar;
    SwipeRefreshLayout refreshLayout;
    EarthquakeAdapter earthQuakeAdapter;
    SharedPreferences sharedPrefs;
    private String TAG = EarthquakeActivity.class.getSimpleName();
    ListView earthquakeListView;
    String minMagnitude;
    String orderBy;
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    /*public static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minlatitude=25&maxlatitude=39&minlongitude=44&maxlongitude=61";*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        forceRTLIfSupported();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Typeface ty1 = Typeface.createFromAsset(getApplication().getAssets(), "fonts/nasimbd.ttf");
        mEmptyState = findViewById(R.id.empty_view);
        mEmptyState.setTypeface(ty1);
        earthquakeListView = findViewById(R.id.list);
        mProgressBar = findViewById(R.id.progressBar);
        refreshLayout = findViewById(R.id.pullToRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                refreshLayout.setRefreshing(false);
            }
        });
        refreshData();
        earthquakeListView.setEmptyView(mEmptyState);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String url = earthQuakes.get(position).getUrl();

                Uri uri = Uri.parse(earthQuakes.get(position).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        earthQuakeAdapter = new EarthquakeAdapter(this, earthQuakes);
        earthquakeListView.setAdapter(earthQuakeAdapter);
    }



    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, Bundle args) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_most_recent_value)
        );
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        uriBuilder.appendQueryParameter("minlatitude", "25");
        uriBuilder.appendQueryParameter("maxlatitude", "39");
        uriBuilder.appendQueryParameter("minlongitude", "44");
        uriBuilder.appendQueryParameter("maxlongitude", "64");
        return new EarthquakeAsyncTaskLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> data) {
        earthQuakeAdapter.clear();
        if (data != null && !data.isEmpty()) {
            earthQuakeAdapter.addAll(data);
            mProgressBar.setVisibility(View.GONE);
        }
        else {
            mEmptyState.setText(R.string.no_earthquake_found);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        earthQuakeAdapter.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_min_magnitude_key)) ||
                key.equals(getString(R.string.settings_order_by_key))){
            // Clear the ListView as a new query will be kicked off
            earthQuakeAdapter.clear();

            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyState.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.progressBar);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, About.class));
                return true;
            case R.id.filter:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("آیا مطمئنید که میخواید خارج بشید ؟")
                .setCancelable(false)
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EarthquakeActivity.this.finish();
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void refreshData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyState.setText(R.string.no_internet_connection);
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }
}