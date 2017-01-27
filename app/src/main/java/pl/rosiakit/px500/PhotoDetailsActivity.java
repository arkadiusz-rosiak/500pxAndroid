package pl.rosiakit.px500;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

import pl.rosiakit.px500.model.Photo;
import pl.rosiakit.px500.provider.NetworkPhotosProvider;
import pl.rosiakit.px500.provider.OnSinglePhotoDownloadedListener;
import pl.rosiakit.px500.provider.PhotosProvider;

public class PhotoDetailsActivity extends OnlineActivity {

    private static final String TAG = "Photo Details";

    public static final String PHOTO_ID_INTENT_KEY = "photo_intent_key";
    public static final String PHOTO_NAME_INTENT_KEY = "photo_name_intent_key";

    private Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);

        Intent i = getIntent();
        int id = (int) i.getExtras().getSerializable(PHOTO_ID_INTENT_KEY);
        String name = (String) i.getExtras().getSerializable(PHOTO_NAME_INTENT_KEY);

        prepareActionBar(name);
        downloadAndShowPhoto(id);
    }

    private void showPhotoDetails(){
        if(photo != null) {

            ImageView photoView = (ImageView) findViewById(R.id.photo_details_image);

            setTextToTextView(R.id.photo_title, photo.getName());
            setTextToTextView(R.id.photo_category, photo.getCategoryName());
            setTextToTextView(R.id.photo_description, photo.getDescription());
            setTextToTextView(R.id.photo_camera, photo.getCamera());
            setTextToTextView(R.id.photo_iso, photo.getIso());
            setTextToTextView(R.id.photo_shutter_speed, photo.getShutterSpeed());
            setTextToTextView(R.id.photo_focal_length, photo.getFocalLength());
            setTextToTextView(R.id.photo_aperture, photo.getAperture());

            Picasso.with(this).load(Uri.parse(photo.getUrl())).into(photoView);
        }
    }

    private void setTextToTextView(int id, CharSequence text){
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
    }

    private void downloadAndShowPhoto(final int id) {
        Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getPhotoFromNetwork(id);
            }
        });

        downloadThread.start();
    }

    private void getPhotoFromNetwork(int id){
        try {
            fetchPhoto(id);
        } catch (IOException e) {
            Log.e(TAG, "IOException while fetching photos", e);
            showToast(R.string.io_exception);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException while fetching photos", e);
            showToast(R.string.server_exception);
        }
    }

    private void fetchPhoto(int id) throws IOException, JSONException {
        PhotosProvider provider = new NetworkPhotosProvider(this);

        provider.downloadPhotoFullSize(id, new OnSinglePhotoDownloadedListener() {
            @Override
            public void onDownloadFinished(final Photo downloadedPhoto) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        photo = downloadedPhoto;
                        showPhotoDetails();
                    }
                });
            }
        });

    }

    private void prepareActionBar(String title) {
        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setTitle(title);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
