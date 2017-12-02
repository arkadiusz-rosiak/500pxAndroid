package pl.rosiakit.px500.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pl.rosiakit.px500.config.Constants;
import pl.rosiakit.px500.model.Photo;
import pl.rosiakit.px500.utils.*;

import static android.content.Context.MODE_PRIVATE;

public class NetworkPhotosProvider implements PhotosProvider {

    private final Context context;

    public NetworkPhotosProvider(Context context) {
        this.context = context;
    }

    @Override
    public void downloadPhotos(int page, String feature, OnPhotosListDownloadedListener listener)
            throws IOException, JSONException {

        String url = buildAllPhotosUrl(page, feature);
        JSONArray photosArray = getPhotosJSONFromURL(url);
        List<Photo> photos = parsePhotosList(photosArray);

        listener.onDownloadFinished(photos);
    }

    private JSONArray getPhotosJSONFromURL(String url) throws IOException, JSONException {
        try {
            String photosRAW = new NetworkRequest(url, "GET").execute();
            JSONObject photosObject = new JSONObject(photosRAW);
            return photosObject.getJSONArray("photos");
        } catch (UserNotAuthorizedException ignored) {
            return null;
        }
    }

    private List<Photo> parsePhotosList(JSONArray photosArray) throws JSONException {
        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < photosArray.length(); ++i) {
            JSONObject photoObj = photosArray.getJSONObject(i);
            Photo photo = parsePhotoFromJsonObject(photoObj);
            photos.add(photo);
        }

        return photos;
    }

    @NonNull
    private Photo parsePhotoFromJsonObject(JSONObject photoObj) throws JSONException {
        return new Photo(photoObj.getInt("id"),
                photoObj.getString("image_url"),
                photoObj.getString("name"),
                photoObj.getString("description"),
                photoObj.getInt("positive_votes_count"),
                photoObj.getBoolean("nsfw"));
    }

    private String buildAllPhotosUrl(int page, String feature) {
        int rpp = getRppFromSettings();
        Set<PhotoCategory> categories = new SelectedCategories(context).getSelectedCategories();

        return new ApiUrlBuilder().getFeature(feature).onlyCategories(categories)
                .resultsLimit(rpp).page(page).build();
    }

    private int getRppFromSettings() {
        SharedPreferences preferences = context.getSharedPreferences("api_settings", MODE_PRIVATE);
        return preferences.getInt(Constants.SETTINGS_RPP_KEY, Constants.DEFAULT_RPP);
    }

    @Override
    public void downloadPhotoFullSize(int id, OnSinglePhotoDownloadedListener listener)
            throws IOException, JSONException {

        String url = new ApiUrlBuilder().setPhotoId(id).build();
        JSONObject photoObj = getSinglePhotosJSONFromUrl(url);

        Photo photo = parsePhotoFromJsonObject(photoObj);
        addDetailsToPhoto(photoObj, photo);

        listener.onDownloadFinished(photo);
    }

    private JSONObject getSinglePhotosJSONFromUrl(String url) throws IOException, JSONException {
        try {
            String photosRAW = new NetworkRequest(url, "GET").execute();
            JSONObject rootObject = new JSONObject(photosRAW);
            return rootObject.getJSONObject("photo");
        } catch (UserNotAuthorizedException ignored) {
            return null;
        }
    }

    private void addDetailsToPhoto(JSONObject photoObj, Photo photo) throws JSONException {
        photo.setCamera(photoObj.getString("camera"));
        photo.setCategory(photoObj.getInt("category"));
        photo.setFocalLength(photoObj.getString("focal_length"));
        photo.setIso(photoObj.getString("iso"));
        photo.setShutterSpeed(photoObj.getString("shutter_speed"));
        photo.setAperture(photoObj.getString("aperture"));
    }
}