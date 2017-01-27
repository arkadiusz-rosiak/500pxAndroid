package pl.rosiakit.px500.provider;

import pl.rosiakit.px500.model.Photo;

public interface OnSinglePhotoDownloadedListener {
    void onDownloadFinished(Photo photo);
}
