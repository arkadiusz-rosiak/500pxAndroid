package pl.rosiakit.px500;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import pl.rosiakit.px500.utils.NetworkRequest;

/**
 * Base class for activities that do something online, like downloading photos etc.
 * It contains some useful methods.
 */
public class OnlineActivity extends AppCompatActivity {

    protected Handler handler = new Handler();

    protected SharedPreferences preferences;

    protected static Set<Long> favourites = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("api_settings", MODE_PRIVATE);
        downloadFavourites();
    }

    protected boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    protected void showToast(final int resId) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OnlineActivity.this, resId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected boolean isLoggedIn() {
        return StringUtils.isNotBlank(preferences.getString("auth_token", ""));
    }

    protected boolean isFavourite(long photoId) {
        if (favourites.isEmpty() && isOnline()) {
            downloadFavourites();
        }

        return favourites.contains(photoId);
    }

    private void downloadFavourites() {
        Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = preferences.getString("auth_token", "");
                    int uid = preferences.getInt("uid", 0);
                    String url = "http://vps2.rosiak.it:8080/favourites/" + uid + "/ids";
                    NetworkRequest networkRequest = new NetworkRequest(url, "GET");
                    networkRequest.addAuthToken(token);
                    String result = networkRequest.execute();
                    JSONArray photosArray = new JSONArray(result);
                    for(int i = 0; i < photosArray.length(); ++i){
                        favourites.add(photosArray.getLong(i));
                    }
                } catch (Exception e) {
                    // ignored
                }
            }
        });

        downloadThread.start();
    }
}
