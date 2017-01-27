package pl.rosiakit.px500.provider;

import java.util.Collection;
import pl.rosiakit.px500.model.Photo;

public interface OnPhotosListDownloadedListener {
    void onDownloadFinished(Collection<Photo> photos);
}
