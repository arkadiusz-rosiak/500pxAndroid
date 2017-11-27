package pl.rosiakit.px500.utils;

import android.support.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkRequest {

    private final String url;
    private final String method;
    private final Map<String, String> params = new HashMap<>();

    public NetworkRequest(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public void addPostParam(String key, String value) {
        params.put(key, value);
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

            if(method.equalsIgnoreCase("post")) {
                OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
                writer.write(getParamsAsString());
                writer.flush();
                writer.close();
                out.close();
            }

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

    private String getParamsAsString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            sb.append(param.getKey()).append('=').append(param.getValue()).append('&');
        }
        return sb.toString();
    }
}
