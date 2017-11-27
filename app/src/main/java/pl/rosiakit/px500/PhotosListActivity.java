package pl.rosiakit.px500;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collection;

import pl.rosiakit.px500.model.Photo;
import pl.rosiakit.px500.provider.NetworkPhotosProvider;
import pl.rosiakit.px500.provider.OnPhotosListDownloadedListener;
import pl.rosiakit.px500.provider.PhotosProvider;

public class PhotosListActivity extends OnlineActivity {

    private static final String TAG = "500PX";
    public static final int REQUEST_CHANE_SETTINGS = 1;
    public static final int REQUEST_ACCOUNT = 2;

    private Handler handler;
    private PhotosAdapter adapter;
    private PhotosProvider photosProvider;

    private String feature = "popular";
    private int currentPage = 1;
    private boolean isDownloadingActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        handler = new Handler();
        adapter = new PhotosAdapter(this);
        photosProvider = new NetworkPhotosProvider(this);

        findViewById(R.id.button_popular).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPhotos("popular");
            }
        });

        findViewById(R.id.button_highest_rated).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPhotos("highest_rated");
            }
        });

        findViewById(R.id.button_fresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPhotos("fresh_today");
            }
        });

        toggleFavButton();

        initializeGrid();
        showPhotos("popular");
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void refreshPhotos(String feature){
        currentPage = 1;
        showPhotos(feature);
    }

    private void showPhotos(String feature) {
        this.feature = feature;

        if(isOnline()) {
            isDownloadingActive = true;
            toggleProgressBar();
            downloadPhotosInNewThread(currentPage++);
        }
        else{
            showToast(R.string.no_internet);
        }
    }

    private void toggleProgressBar(){

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if(progressBar != null) {
            if (isDownloadingActive) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initializeGrid() {
        GridView grid = (GridView) findViewById(R.id.photo_grid);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Photo photo = adapter.getItem(position);

            if (isOnline()) {
                showPhoto(photo);
            } else {
                showToast(R.string.no_internet);
            }
            }
        });

        grid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // do nothing
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(view.getLastVisiblePosition() >= (totalItemCount-4) && currentPage > 1 && !isDownloadingActive){
                    Log.v(TAG,"Pobieranko");
                    showPhotos(feature);
                }
            }
        });

    }

    private void showPhoto(Photo photo) {
        Intent i = new Intent(this, PhotoDetailsActivity.class);
        i.putExtra(PhotoDetailsActivity.PHOTO_ID_INTENT_KEY, photo.getId());
        i.putExtra(PhotoDetailsActivity.PHOTO_NAME_INTENT_KEY, photo.getName());
        startActivity(i);
    }

    private void downloadPhotosInNewThread(final int page) {
        Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getPhotosFromNetwork(page);
            }
        });

        downloadThread.start();
    }

    private void getPhotosFromNetwork(int page) {
        try {
            fetchPhotos(page);
        } catch (IOException e) {
            Log.e(TAG, "IOException while fetching photos", e);
            showToast(R.string.io_exception);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException while fetching photos", e);
            showToast(R.string.server_exception);
        }
    }

    private void fetchPhotos(final int page) throws IOException, JSONException {

        photosProvider.downloadPhotos(page, feature, new OnPhotosListDownloadedListener() {
            @Override
            public void onDownloadFinished(final Collection<Photo> photos) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG, "Pobrano " + photos.size());

                    if(page == 1){
                        adapter.setPhotos(photos);
                    }
                    else{
                        adapter.addPhotos(photos);
                    }

                    adapter.notifyDataSetChanged();
                    setTitleBasedOnFeature();

                    if(photos.size() > 0) {
                        isDownloadingActive = false;
                        toggleProgressBar();
                    }
                    else{
                        findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
                    }
                }
            });
            }
        });
    }

    private void setTitleBasedOnFeature() {
        String title = "Popular on 500px.com";

        title = ("highest_rated".equals(feature)) ? "Highest rated on 500px.com" : title;
        title = ("fresh_today".equals(feature)) ? "Todays fresh on 500px.com" : title;

        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings: {

                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, REQUEST_CHANE_SETTINGS);

                return true;
            }

            case R.id.action_account: {

                Intent i = new Intent(this, AccountActivity.class);
                startActivityForResult(i, REQUEST_ACCOUNT);

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CHANE_SETTINGS && resultCode == RESULT_OK){
            refreshPhotos(feature);
        }

        toggleFavButton();

    }

    private void toggleFavButton() {
        if(!isLoggedIn()) {
            findViewById(R.id.button_fav).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.button_fav).setVisibility(View.VISIBLE);
        }
    }
}
