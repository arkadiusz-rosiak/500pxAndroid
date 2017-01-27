package pl.rosiakit.px500.provider;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import pl.rosiakit.px500.model.Photo;

public interface PhotosProvider {

    void downloadPhotos(int page, String feature, OnPhotosListDownloadedListener listener) throws IOException, JSONException;

    void downloadPhotoFullSize(int id, OnSinglePhotoDownloadedListener listener) throws IOException, JSONException;

}
