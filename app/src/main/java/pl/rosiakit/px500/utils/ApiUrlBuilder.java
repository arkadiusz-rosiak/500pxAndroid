package pl.rosiakit.px500.utils;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.rosiakit.px500.config.Constants;

public class ApiUrlBuilder {

    private static final String BASE_URL = "https://api.500px.com/v1/photos";

    private int photoId = Integer.MIN_VALUE;

    private int rpp = 20;
    private int page = 1;
    private String feature = "popular";
    private List<PhotoCategory> excluded = new ArrayList<>();
    private List<PhotoCategory> only = new ArrayList<>();

    public ApiUrlBuilder setPhotoId(int photoId){
        this.photoId = photoId;
        return this;
    }

    public ApiUrlBuilder resultsLimit(int limit) {
        this.rpp = limit;
        return this;
    }

    public ApiUrlBuilder page(int page) {
        this.page = page;
        return this;
    }

    public ApiUrlBuilder getFeature(String feature) {
        this.feature = feature;
        return this;
    }

    public ApiUrlBuilder onlyCategories(Collection<PhotoCategory> categories){
        only.clear();
        only.addAll(categories);
        return this;
    }

    public String build(){
        if(photoId > 0){
            return singlePhotoUrl();
        }

        return photoListUrl();
    }

    @NonNull
    private String photoListUrl() {

        StringBuilder sb = new StringBuilder(BASE_URL + "?consumer_key="+ Constants.CONSUMER_KEY_500PX);

        sb.append("&feature=").append(feature);
        sb.append("&rpp=").append(rpp);
        sb.append("&page=").append(page);

        if(only.size() > 0){
            sb.append("&only=").append(joinCategoriesList(only));
        }
        else if(excluded.size() > 0){
            sb.append("&exclude=").append(joinCategoriesList(excluded));
        }

        return sb.toString();
    }

    @NonNull
    private String joinCategoriesList(List<PhotoCategory> elements) {

        StringBuilder sb = new StringBuilder(urlEncode(elements.get(0).toString()));

        for(int i = 1; i < elements.size(); ++i){
            sb.append(',').append(urlEncode(elements.get(i).toString()));
        }

        return sb.toString();
    }

    private String urlEncode(String value){
        return Uri.encode(value);
    }

    private String singlePhotoUrl(){
        return BASE_URL + "/" + photoId + "?consumer_key="+ Constants.CONSUMER_KEY_500PX;
    }

}
