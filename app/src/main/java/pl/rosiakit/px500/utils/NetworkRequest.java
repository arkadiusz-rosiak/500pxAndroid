package pl.rosiakit.px500.utils;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkRequest {

    private final String url;
    private final String method;

    public NetworkRequest(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String execute() throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod(method);
            conn.setDoInput(true);

            conn.connect();
            is = conn.getInputStream();

            return readStream(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e){
                    // do nothing, stream is already closed
                }
            }
        }
    }

    @NonNull
    private String readStream(InputStream stream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(stream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        return total.toString();
    }
}
