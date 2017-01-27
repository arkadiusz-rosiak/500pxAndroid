package pl.rosiakit.px500;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.rosiakit.px500.model.Photo;

class PhotosAdapter extends BaseAdapter {


    private List<Photo> photos = new ArrayList<>();
    private Context context;

    PhotosAdapter(Context context) {
        this.context = context;
    }

    void setPhotos(Collection<Photo> photos){
        this.photos.clear();
        this.photos.addAll(photos);
    }

    void addPhotos(Collection<Photo> photos){
        this.photos.addAll(photos);
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Photo getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View photoView;

        if (convertView == null) {
            photoView = LayoutInflater.from(context).inflate(R.layout.photos_row, parent, false);
        } else {
            photoView = convertView;
        }

        Photo photo = getItem(position);

        ImageView photoPlace = (ImageView) photoView.findViewById(R.id.photo_row_image);

        if(photo.isNSFW()){
            photoPlace.setImageResource(R.drawable.nsfw);
        }
        else {
            Picasso.with(context).load(Uri.parse(photo.getUrl())).into(photoPlace);
        }

        TextView rating = (TextView) photoView.findViewById(R.id.photo_rating);
        rating.setText(context.getString(R.string.upvotes) + photo.getVotes());

        return photoView;
    }


}
